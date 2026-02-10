package com.dansmultipro.opsapps.util;

import io.github.bucket4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterUtil {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Map<String, Integer> burstMap =  new ConcurrentHashMap<>();
    private final Map<String, Instant> penaltyMap = new ConcurrentHashMap<>();

    public boolean tryConsume(String ip) {
        Bucket bucket = resolveBucket(ip);
        return bucket.tryConsume(1);
    }

    public Duration getNewDuration(String ip) {
        Instant now = Instant.now();
        Instant penaltyExpiresAt = penaltyMap.get(ip);

        if (penaltyExpiresAt != null && now.isBefore(penaltyExpiresAt)) {
            int currentBurst = burstMap.getOrDefault(ip, 0);
            return Duration.ofMinutes(3).multipliedBy(currentBurst);
        }

        Integer burstCount = burstMap.compute(ip, (key,value) -> value == null ? 1 : value + 1);
        Duration base = Duration.ofMinutes(3);
        Duration newDuration = base.multipliedBy(burstCount);
        penaltyMap.put(ip, now.plus(newDuration));

        return newDuration;
    }

    public void extendRefill(String ip, Duration newDuration) {

        Bucket bucket = resolveBucket(ip);

        BucketConfiguration newConfig = BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(5).refillIntervally(5, newDuration))
                .build();

        bucket.replaceConfiguration(newConfig, TokensInheritanceStrategy.AS_IS);
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(5).refillIntervally(5, Duration.ofMinutes(3)))
                .build();
    }

    public void resetBucket(String ip) {
        cache.remove(ip);
        burstMap.remove(ip);
        penaltyMap.remove(ip);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleReset() {
        cache.clear();
        burstMap.clear();
        penaltyMap.clear();
    }
}

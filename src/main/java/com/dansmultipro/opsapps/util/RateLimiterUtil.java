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
    private final Map<String, PenaltyInfo> penaltyMap = new ConcurrentHashMap<>();

    public boolean tryConsume(String ip) {
        Bucket bucket = resolveBucket(ip);
        return bucket.tryConsume(1);
    }

    public Duration getNewDuration(String ip) {
        PenaltyInfo info = penaltyMap.get(ip);

        if (info == null || info.isExpired()) {
            int newLevel = (info == null) ? 1 : info.level + 1;

            Duration newDuration = calculateDuration(newLevel);
            penaltyMap.put(ip, new PenaltyInfo(newLevel, newDuration));

            return newDuration;
        }

        return info.penaltyDuration;
    }

    public void extendRefill(String ip, Duration newDuration) {

        cache.remove(ip);

        Bucket newBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, newDuration)))
                .build();

        cache.put(ip, newBucket);
    }

    private Duration calculateDuration(int level) {
        return switch (level) {
            case 1 -> Duration.ofMinutes(3);
            case 2 -> Duration.ofMinutes(6);
            case 3 -> Duration.ofMinutes(15);
            case 4 -> Duration.ofMinutes(30);
            default -> Duration.ofHours(1);
        };
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        PenaltyInfo info = penaltyMap.get(ip);
        Duration duration;

        if (info != null && !info.isExpired()) {
            duration = info.penaltyDuration;
        } else {
            duration = Duration.ofMinutes(1);
        }

        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, duration)))
                .build();
    }

    @Scheduled(fixedRate = 600000)
    public void cleanup() {

        penaltyMap.entrySet().removeIf(entry -> entry.getValue().isExpired());

        cache.keySet().removeIf(ip -> !penaltyMap.containsKey(ip));
    }

    private static class PenaltyInfo {
        final int level;
        final Duration penaltyDuration;
        final Instant expiryTime;

        PenaltyInfo(int level, Duration penaltyDuration) {
            this.level = Math.min(level, 5);
            this.penaltyDuration = penaltyDuration;

            this.expiryTime = Instant.now().plus(penaltyDuration).plus(penaltyDuration);
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }
}

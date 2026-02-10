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

    private static final int BASE_CAPACITY = 5;
    private static final Duration BASE_DURATION = Duration.ofMinutes(1);

    private static final Duration MAX_PENALTY_DURATION = Duration.ofHours(1);
    private static final int MAX_PENALTY_LEVEL = 5;


    public boolean tryConsume(String ip) {
        Bucket bucket = resolveBucket(ip);
        return bucket.tryConsume(1);
    }

    public Duration getNewDuration(String ip) {
        PenaltyInfo info = penaltyMap.get(ip);

        if (info == null || info.isExpired()) {
            Duration newDuration = calculateDuration(1);
            penaltyMap.put(ip, new PenaltyInfo(1, newDuration));

            return newDuration;
        } else {

            int newLevel = Math.min(info.level + 1, MAX_PENALTY_LEVEL);

            Duration newDuration = calculateDuration(newLevel);
            penaltyMap.put(ip, new PenaltyInfo(newLevel, newDuration));

            return newDuration;
        }
    }

    public void extendRefill(String ip, Duration newDuration) {
        cache.remove(ip);

        Bucket newBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(BASE_CAPACITY, Refill.intervally(BASE_CAPACITY, newDuration)))
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
            duration = BASE_DURATION;
        }

        return Bucket.builder()
                .addLimit(Bandwidth.classic(BASE_CAPACITY, Refill.intervally(BASE_CAPACITY, duration)))
                .build();
    }

    @Scheduled(fixedRate = 600000)
    public void cleanup() {

        penaltyMap.entrySet().removeIf(entry -> entry.getValue().isExpired());

        cache.keySet().removeIf(ip -> {
            PenaltyInfo info = penaltyMap.get(ip);
            return info == null || info.isExpired();
        });
    }

    private static class PenaltyInfo {
        final int level;
        final Duration penaltyDuration;
        final Instant expiryTime;

        PenaltyInfo(int level, Duration penaltyDuration) {
            this.level = Math.min(level, MAX_PENALTY_LEVEL);
            this.penaltyDuration = penaltyDuration;

            this.expiryTime = Instant.now().plus(penaltyDuration);
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }
}

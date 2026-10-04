package com.example.rateLimiter.strategy;

import com.example.rateLimiter.entity.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class SlidingWindowCounter implements RateLimiterStrategy {

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final TimeSource tm;
    public SlidingWindowCounter(TimeSource tm1){
        this.tm = tm1;
    }

    @Override
    public RateLimitResponse isAllowed(String bucketKey, int maxRequests, int windowSeconds) {
        long nowSeconds = tm.nanoSeconds() / 1_000_000_000L;
        Window window = windows.computeIfAbsent(bucketKey, k -> new Window(nowSeconds));

        window.lock.lock();
        try {
            long elapsed = nowSeconds - window.originSeconds;
            long currentWindowIndex = elapsed / windowSeconds;
            long elapsedInCurrentWindow = elapsed % windowSeconds;
            long windowGap = currentWindowIndex - window.currentWindowIndex;

            if (windowGap == 1) {

                window.previousCount = window.currentCount;
                window.currentCount = 0;
                window.currentWindowIndex = currentWindowIndex;
            } else if (windowGap > 1) {

                window.previousCount = 0;
                window.currentCount = 0;
                window.currentWindowIndex = currentWindowIndex;
            }


            double previousWeight = 1.0 - ((double) elapsedInCurrentWindow / windowSeconds);
            double weightedCount = (previousWeight * window.previousCount) + window.currentCount;

            if (weightedCount < maxRequests) {
                window.currentCount += 1;
                int remaining = Math.max((int) Math.floor(maxRequests - weightedCount - 1), 0);
                return new RateLimitResponse(true, maxRequests, remaining);
            }
            return new RateLimitResponse(false, maxRequests, 0);
        } finally {
            window.lock.unlock();
        }
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.SLIDING_WINDOW_COUNTER;
    }

    private static class Window {
        final long originSeconds;
        final ReentrantLock lock = new ReentrantLock();
        long currentWindowIndex;
        int previousCount;
        int currentCount;

        Window(long originSeconds) {
            this.originSeconds = originSeconds;
        }
    }
}

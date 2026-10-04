package com.example.rateLimiter.strategy;

public interface TimeSource {
    public long nanoSeconds();
}

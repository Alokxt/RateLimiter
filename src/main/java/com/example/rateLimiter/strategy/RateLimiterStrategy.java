package com.example.rateLimiter.strategy;

import com.example.rateLimiter.entity.AlgorithmType;

public interface RateLimiterStrategy {
    RateLimitResponse isAllowed(String BucketKey,int allowed_requests,int window_Seconds);
    AlgorithmType getType();
}

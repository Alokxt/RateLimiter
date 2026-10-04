package com.example.rateLimiter.strategy;

public record RateLimitResponse(boolean allowed,int limit,int remaining){
}

package com.example.rateLimiter.strategy;

import org.springframework.stereotype.Component;

import java.sql.Time;

@Component
public class SystemTimeSource implements TimeSource {
    @Override
    public long nanoSeconds() {
        return System.nanoTime();
    }
}

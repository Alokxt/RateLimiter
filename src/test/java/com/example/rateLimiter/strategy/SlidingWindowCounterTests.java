package com.example.rateLimiter.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;



import static org.junit.jupiter.api.Assertions.*;

public class SlidingWindowCounterTests {
    private TimeSource timeSource;
    private SlidingWindowCounter limiter;
    private long currentNanos;

    @BeforeEach
    void setUp() {
        currentNanos = 0L;
        timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(timeSource.nanoSeconds()).thenAnswer(inv -> currentNanos);
        limiter = new SlidingWindowCounter(timeSource);
    }

    @Test
    void limitingAfterCapacity() {
        for (int i = 0; i < 5; i++) assertTrue(limiter.isAllowed("k", 5, 10).allowed());
        assertFalse(limiter.isAllowed("k", 5, 10).allowed());
    }

    @Test
    void windowBoundaries() {
        for (int i = 0; i < 5; i++) limiter.isAllowed("k", 5, 10);

        currentNanos += 12_000_000_000L;

        RateLimitResponse r = limiter.isAllowed("k", 5, 10);

        assertTrue(r.allowed());
        assertTrue(r.remaining() < 4, "remaining should reflect carried-over weight, not a full reset");
    }
}

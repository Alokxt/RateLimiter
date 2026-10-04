package com.example.rateLimiter.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

public class SlidingWindowLogTests {
    private SlidingWindowLog limiter;
    private long currNanos;
    private TimeSource timeSource;

    @BeforeEach
    void setUp(){
        currNanos = 0L;
        timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(timeSource.nanoSeconds()).thenAnswer(inv->currNanos);
        limiter = new SlidingWindowLog(timeSource);

    }

    @Test
    void limitingAfterCapacity(){
        for(int i=1;i<6;i++){
            RateLimitResponse r = limiter.isAllowed("key1",5,10);
            assertTrue(r.allowed(), "request " + i + " should be allowed");
        }
        RateLimitResponse r2 = limiter.isAllowed("key1",5,10);
        assertFalse(r2.allowed());
        assertEquals(0,r2.remaining());

    }
    @Test
    void exactRecoveryAfterWindowFullyExpires() {
        for (int i = 0; i < 5; i++) limiter.isAllowed("k", 5, 10);
        assertFalse(limiter.isAllowed("k", 5, 10).allowed());

        currNanos += 11_000_000_000L;

        RateLimitResponse r = limiter.isAllowed("k", 5, 10);
        assertTrue(r.allowed());
        assertEquals(4, r.remaining(), "log should be fully cleared, not a partial blend");
    }

    @Test
    void onlyExpiresEntriesOlderThanWindow() {
        limiter.isAllowed("k", 5, 10);
        currNanos += 6_000_000_000L;
        limiter.isAllowed("k", 5, 10);
        currNanos += 5_000_000_000L;

        RateLimitResponse r = limiter.isAllowed("k", 5, 10);
        assertTrue(r.allowed());
        assertEquals(3, r.remaining(), "only the expired entry should drop out, not both");
    }



}

package com.example.rateLimiter.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class TokenBucketTests {
    private TimeSource timeSource;
    private TokenBucket limiter;
    private long currSeconds;

    @BeforeEach
    void setUp(){
        currSeconds = 0L;
        timeSource = Mockito.mock(TimeSource.class);
        Mockito.when(timeSource.nanoSeconds()).thenAnswer(inv -> currSeconds);
        limiter = new TokenBucket(timeSource);
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
    void refillLogic(){
        for(int i=0;i<5;i++){
            RateLimitResponse r = limiter.isAllowed("key1",5,10);
        }
        assertFalse(limiter.isAllowed("key1", 5, 10).allowed());
        currSeconds += 2_000_000_000L;
        RateLimitResponse r = limiter.isAllowed("key1",5,10);
        assertTrue(r.allowed());


    }

    @Test
    void keyIndependence(){
        for(int i=1;i<7;i++){
            RateLimitResponse r = limiter.isAllowed("key2",5,10);
        }
        RateLimitResponse r2 = limiter.isAllowed("key3",5,10);
        assertTrue(r2.allowed());
    }

    @Test
    void concurrentRequestsHandled() throws InterruptedException{
        int numThreads = 50;
        int limit = 10;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger allowedCount = new AtomicInteger();
        for(int i=0;i<numThreads;i++){
            pool.submit(()->{
                try{
                    if(limiter.isAllowed("key4",limit,10).allowed()){
                        allowedCount.incrementAndGet();
                    }
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        pool.shutdown();
        assertEquals(limit,allowedCount.get(),"exactly the limit should succeed, no more");

    }

}

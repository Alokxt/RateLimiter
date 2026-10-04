package com.example.rateLimiter.strategy;

import com.example.rateLimiter.entity.AlgorithmType;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class TokenBucket implements RateLimiterStrategy {

    private final ConcurrentHashMap<String , Bucket> buckets = new ConcurrentHashMap<>();
    private final TimeSource tm;
    public TokenBucket(TimeSource tm1){
        this.tm = tm1;
    }

    @Override
    public RateLimitResponse isAllowed(String BucketKey,int allowed_requests,int window_seconds){
        Bucket bucket = buckets.computeIfAbsent(BucketKey,k->new Bucket(allowed_requests,tm.nanoSeconds()));
        bucket.lock.lock();
        refillBucket(bucket,allowed_requests,window_seconds);
        try{
            if(bucket.tokens >=1){
                bucket.tokens -= 1;
                return new RateLimitResponse(true,allowed_requests,(int) bucket.tokens);
            }
            return new RateLimitResponse(false,allowed_requests,0);
        }finally {
            bucket.lock.unlock();
        }

    }

    private void refillBucket(Bucket bucket,double capacity,int windowSeconds){
        long now = tm.nanoSeconds();
        long tm = (now - bucket.lastRefill) / 1_000_000_000;
        double refillRate  = (double )capacity /windowSeconds;
        double tokens = refillRate*tm;

        if(tokens>0){
            bucket.tokens  = Math.min(capacity,bucket.tokens+tokens);
            bucket.lastRefill = now;

        }

    }

    @Override
    public AlgorithmType getType(){
        return AlgorithmType.TOKEN_BUCKET;
    }

    private static class Bucket{
        volatile double tokens;
        volatile long lastRefill;
        final ReentrantLock lock = new ReentrantLock();

        Bucket(double tokens,long refill){
            this.tokens = tokens;
            this.lastRefill = refill;
        }
    }
}



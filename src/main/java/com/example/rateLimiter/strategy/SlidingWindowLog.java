package com.example.rateLimiter.strategy;

import com.example.rateLimiter.entity.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class SlidingWindowLog implements RateLimiterStrategy{
    private final ConcurrentHashMap<String,Log> mp = new ConcurrentHashMap<>();
    private final TimeSource tm;
    public SlidingWindowLog(TimeSource tm1){
        this.tm = tm1;
    }

    @Override
    public RateLimitResponse isAllowed(String key,int allowed_requests,int window_Seconds){
        Log log = mp.computeIfAbsent(key,k-> new Log());
        long now = tm.nanoSeconds()/ 1_000_000_000L;
        log.lock.lock();
        try{
            while(!log.logs.isEmpty() && now-log.logs.peekFirst() > window_Seconds){
                log.logs.pollFirst();
            }
            if(log.logs.size() < allowed_requests){
                log.logs.addLast(now);
                int remaining = allowed_requests - log.logs.size();
                return new RateLimitResponse(true, allowed_requests, remaining);

            }
            return new RateLimitResponse(false, allowed_requests, 0);
        }finally {
            log.lock.unlock();
        }
    }

    @Override
    public AlgorithmType getType(){
        return AlgorithmType.SLIDING_WINDOW_LOG;
    }

    private static class Log{
        final Deque<Long> logs = new ArrayDeque<>();
        final ReentrantLock lock = new ReentrantLock();
    }
}

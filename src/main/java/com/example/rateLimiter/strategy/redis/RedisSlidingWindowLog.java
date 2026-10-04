package com.example.rateLimiter.strategy.redis;

import com.example.rateLimiter.entity.AlgorithmType;
import com.example.rateLimiter.strategy.RateLimitResponse;
import com.example.rateLimiter.strategy.RateLimiterStrategy;
import com.example.rateLimiter.strategy.TimeSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public class RedisSlidingWindowLog implements RateLimiterStrategy {
    private final TimeSource timeSource;
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> script;

    public RedisSlidingWindowLog(TimeSource timeSource1,StringRedisTemplate redisTemplate1){
        this.timeSource = timeSource1;
        this.redisTemplate = redisTemplate1;
        this.script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/sliding_window_counter.lua"));
        script.setResultType(List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResponse isAllowed(String bucketKey, int allowedRequests, int windowSeconds){
        long now = timeSource.nanoSeconds() / 1_000_000_000L;
        List<Object> result = redisTemplate.execute(
                script,
                List.of(bucketKey),
                String.valueOf(now),
                String.valueOf(allowedRequests),
                String.valueOf(windowSeconds)
        );



        boolean allowed = "1".equals(result.get(0).toString());
        double tokens = Double.parseDouble(result.get(1).toString());

        return new RateLimitResponse(allowed, allowedRequests, (int) tokens);
    }

    @Override
    public AlgorithmType getType(){
        return AlgorithmType.SLIDING_WINDOW_LOG;
    }
}

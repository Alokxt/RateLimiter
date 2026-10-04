package com.example.rateLimiter.strategy.redis;

import com.example.rateLimiter.entity.AlgorithmType;
import com.example.rateLimiter.strategy.RateLimitResponse;
import com.example.rateLimiter.strategy.RateLimiterStrategy;
import com.example.rateLimiter.strategy.TimeSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.sql.Time;
import java.util.List;

public class RedisTokenBucket implements RateLimiterStrategy {

    private final StringRedisTemplate redisTemplate;
    private final TimeSource timeSource;
    private final DefaultRedisScript<List> script;

    public RedisTokenBucket(StringRedisTemplate stringRedisTemplate, TimeSource timeSource){
        this.redisTemplate = stringRedisTemplate;
        this.timeSource = timeSource;
        this.script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/token_bucket.lua"));
        script.setResultType(List.class);

    }

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResponse isAllowed(String bucketKey, int allowedRequests, int windowSeconds){
        long now = timeSource.nanoSeconds() / 1_000_000_000L;
        List<Object> result = redisTemplate.execute(
                script,
                List.of(bucketKey),
                String.valueOf(allowedRequests),
                String.valueOf(windowSeconds),
                String.valueOf(now)
        );
        System.out.println("Answer Redis : "+result.get(0));


        boolean allowed = "1".equals(result.get(0).toString());
        double tokens = Double.parseDouble(result.get(1).toString());

        return new RateLimitResponse(allowed, allowedRequests, (int) tokens);
    }

    @Override
    public AlgorithmType getType(){
        return AlgorithmType.TOKEN_BUCKET;
    }
}

package com.example.rateLimiter.config;

import com.example.rateLimiter.strategy.*;
import com.example.rateLimiter.strategy.redis.RedisSlidingWindowCounter;
import com.example.rateLimiter.strategy.redis.RedisSlidingWindowLog;
import com.example.rateLimiter.strategy.redis.RedisTokenBucket;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class StrategyConfig {
    @Bean
    @ConditionalOnProperty(name="ratelimiter.storage",havingValue="memory",matchIfMissing=true)
    public RateLimiterStrategy tokenBucketMemory(TimeSource timeSource) {
        return new TokenBucket(timeSource);
    }

    @Bean
    @ConditionalOnProperty(name = "ratelimiter.storage", havingValue = "redis")
    public RateLimiterStrategy tokenBucketRedis(StringRedisTemplate redisTemplate, TimeSource timeSource) {
        return new RedisTokenBucket(redisTemplate, timeSource);
    }

    @Bean
    @ConditionalOnProperty(name="ratelimiter.storage",havingValue = "redis")
    public RateLimiterStrategy slidingWindowCounterRedis(TimeSource timeSource,StringRedisTemplate redisTemplate){
        return new RedisSlidingWindowCounter(timeSource,redisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name="ratelimiter.storage",havingValue = "memory",matchIfMissing = true)
    public RateLimiterStrategy slidingWindowCounterMemory(TimeSource timeSource){
        return new SlidingWindowCounter(timeSource);
    }

    @Bean
    @ConditionalOnProperty(name="ratelimiter.storage",havingValue = "redis")
    public RateLimiterStrategy slidingWindowLogRedis(TimeSource timeSource,StringRedisTemplate redisTemplate){
        return new RedisSlidingWindowLog(timeSource,redisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name="ratelimiter.storage",havingValue = "memory",matchIfMissing = true)
    public RateLimiterStrategy slidingWindowLogMemory(TimeSource timeSource){
        return new SlidingWindowLog(timeSource);
    }





}

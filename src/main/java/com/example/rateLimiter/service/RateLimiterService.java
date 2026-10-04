package com.example.rateLimiter.service;

import com.example.rateLimiter.service.EndpointMatcher;
import com.example.rateLimiter.entity.AlgorithmType;
import com.example.rateLimiter.entity.BackendService;
import com.example.rateLimiter.entity.EndpointConfig;
import com.example.rateLimiter.exceptions.InvalidApiKeyException;
import com.example.rateLimiter.repository.BackendServiceRepo;
import com.example.rateLimiter.repository.EndpointConfigRepo;
import com.example.rateLimiter.strategy.RateLimitResponse;
import com.example.rateLimiter.strategy.RateLimiterStrategy;
import com.example.rateLimiter.util.HashUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RateLimiterService {
    private final Map<AlgorithmType, RateLimiterStrategy> strategies;
    private final BackendServiceRepo backendServiceRepo;
    private final EndpointConfigRepo endpointConfigRepo;

    private final HashUtil hashUtil;
    private final EndpointMatcher endpointMatcher;
    public RateLimiterService(List<RateLimiterStrategy> strategyBeans,BackendServiceRepo backendServiceRepo1,HashUtil hashUtil1,EndpointMatcher endpointMatcher1,EndpointConfigRepo endpointConfigRepo1){
        this.strategies = strategyBeans.stream().collect(Collectors.toMap(RateLimiterStrategy::getType,s->s));
        this.endpointConfigRepo = endpointConfigRepo1;
        this.backendServiceRepo = backendServiceRepo1;
        this.hashUtil = hashUtil1;
        this.endpointMatcher = endpointMatcher1;

    }

    public RateLimitResponse checkAllowed(String apiKey, String route){

            String hashedKey = this.hashUtil.sha256Hex(apiKey);
            BackendService backend = backendServiceRepo.findByKeyHash(hashedKey).orElseThrow(()-> new InvalidApiKeyException("Invalid Api Key"));

            List<EndpointConfig> configs = endpointConfigRepo.findByBackendServiceId(backend.getId());
            Optional<EndpointConfig> matches = endpointMatcher.match(configs, route);

            String bucketKey;
            RateLimiterStrategy strategy;
            int allowedRequests;
            int windowSeconds;

            if (matches.isPresent()) {
                EndpointConfig endpoint = matches.get();
                allowedRequests = endpoint.getAllowedRequests();
                strategy = this.strategies.get(endpoint.getAlgorithmType());
                windowSeconds = endpoint.getWindowSeconds();
                bucketKey = backend.getId() + ":" + endpoint.getEndpointPath();
            } else {
                strategy = this.strategies.get(AlgorithmType.TOKEN_BUCKET);
                allowedRequests = backend.getAllowedRequests();
                windowSeconds = backend.getDefaultWindowSecond();
                bucketKey = String.valueOf(backend.getId());
            }

            return strategy.isAllowed(bucketKey, allowedRequests, windowSeconds);

    }


}

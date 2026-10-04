package com.example.rateLimiter.service;

import com.example.rateLimiter.entity.EndpointConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Optional;

@Component
public class EndpointMatcher {
    private final AntPathMatcher matcher = new AntPathMatcher();

    public Optional<EndpointConfig> match(List<EndpointConfig> configs, String incomingPath) {

        Optional<EndpointConfig> exact = configs.stream()
                .filter(c -> c.getEndpointPath().equals(incomingPath))
                .findFirst();

        if (exact.isPresent()) return exact;


        return configs.stream()
                .filter(c -> matcher.match(c.getEndpointPath(), incomingPath))
                .findFirst();
    }
}

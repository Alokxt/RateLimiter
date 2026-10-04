package com.example.rateLimiter.controller;

import com.example.rateLimiter.dto.CheckRequest;
import com.example.rateLimiter.dto.RegisterRequest;
import com.example.rateLimiter.entity.BackendService;
import com.example.rateLimiter.service.RateLimiterService;
import com.example.rateLimiter.strategy.RateLimitResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {
    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService1){
        this.rateLimiterService = rateLimiterService1;
    }

    @PostMapping("/check")
    ResponseEntity<?> check(@RequestHeader("X-API-Key") String apiKey, @Valid @RequestBody CheckRequest  request){
        RateLimitResponse response = rateLimiterService.checkAllowed(apiKey,request.getRoute());
        HttpStatus status = response.allowed() ? HttpStatus.OK : HttpStatus.TOO_MANY_REQUESTS;
        return ResponseEntity.status(status)
                .header("X-RateLimit-Limit", String.valueOf(response.limit()))
                .header("X-RateLimit-Remaining", String.valueOf(response.remaining()))
                .build();
    }

}

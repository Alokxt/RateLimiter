package com.example.rateLimiter.controller;

import com.example.rateLimiter.dto.RegisterEndpointRequest;
import com.example.rateLimiter.dto.RegisterRequest;
import com.example.rateLimiter.entity.EndpointConfig;
import com.example.rateLimiter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService1){
        this.authService = authService1;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String key = authService.register(request);
        return ResponseEntity.ok(key);
    }

    @PostMapping("/register-endpoint")
    public ResponseEntity<?> registerEndpoint(@RequestHeader("X-API-Key") String apiKey,
                                              @Valid @RequestBody RegisterEndpointRequest request) {
        EndpointConfig config = authService.registerEndpoint(apiKey, request);
        return ResponseEntity.ok(Map.of(
                "endpointPath", config.getEndpointPath(),
                "algorithmType", config.getAlgorithmType(),
                "allowedRequests", config.getAllowedRequests(),
                "windowSeconds", config.getWindowSeconds()
        ));
    }



}

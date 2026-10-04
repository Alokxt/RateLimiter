package com.example.rateLimiter.service;

import com.example.rateLimiter.dto.RegisterEndpointRequest;
import com.example.rateLimiter.dto.RegisterRequest;
import com.example.rateLimiter.entity.BackendService;
import com.example.rateLimiter.entity.EndpointConfig;
import com.example.rateLimiter.exceptions.InvalidApiKeyException;
import com.example.rateLimiter.repository.BackendServiceRepo;
import com.example.rateLimiter.repository.EndpointConfigRepo;
import com.example.rateLimiter.util.ApiKeyGenerator;
import com.example.rateLimiter.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ApiKeyGenerator apiKeyGenerator;
    private final HashUtil hashUtil;
    private final BackendServiceRepo backendServiceRepo;
    private final EndpointConfigRepo endpointConfigRepo;

    public AuthService(ApiKeyGenerator apiKeyGenerator1,HashUtil hashUtil1,BackendServiceRepo backendServiceRepo1,EndpointConfigRepo endpointConfigRepo1){
        this.apiKeyGenerator = apiKeyGenerator1;
        this.backendServiceRepo = backendServiceRepo1;
        this.hashUtil = hashUtil1;
        this.endpointConfigRepo = endpointConfigRepo1;

    }

    public String register(RegisterRequest request){
        String rawKey = apiKeyGenerator.generateRawKey();
        String hashedKey = hashUtil.sha256Hex(rawKey);
        BackendService backend = BackendService.builder().company(request.getCompany())
                .origin(request.getOrigin()).defaultWindowSecond(request.getDefaultWindowSecond())
                .allowedRequests(request.getAllowedRequests()).keyHash(hashedKey)
                .build();
        backendServiceRepo.save(backend);
        return rawKey;

    }
    public EndpointConfig registerEndpoint(String apiKey, RegisterEndpointRequest request) {
        String hashedKey = hashUtil.sha256Hex(apiKey);
        BackendService backend = backendServiceRepo.findByKeyHash(hashedKey)
                .orElseThrow(() -> new InvalidApiKeyException("Invalid Api Key"));

        if (endpointConfigRepo.existsByBackendServiceIdAndEndpointPath(backend.getId(), request.getEndpointPath())) {
            throw new IllegalArgumentException("This endpoint is already registered for your service");
        }

        EndpointConfig config = EndpointConfig.builder()
                .backendService(backend)
                .endpointPath(request.getEndpointPath())
                .algorithmType(request.getAlgorithmType())
                .allowedRequests(request.getAllowedRequests())
                .windowSeconds(request.getWindowSeconds())
                .build();

        return endpointConfigRepo.save(config);
    }

    public boolean CheckKey(Long id,String rawKey){
        String hashed = hashUtil.sha256Hex(rawKey);
        BackendService backend = backendServiceRepo.findByKeyHash(hashed).orElseThrow();
        return id.equals(backend.getId());
    }
}

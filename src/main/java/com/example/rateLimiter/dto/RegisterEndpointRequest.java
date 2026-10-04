package com.example.rateLimiter.dto;

import com.example.rateLimiter.entity.AlgorithmType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RegisterEndpointRequest {

    @NotBlank(message = "endpoint path is required")
    private String endpointPath;

    @NotNull(message = "algorithm type is required")
    private AlgorithmType algorithmType;

    @NotNull(message = "allowed requests is required")
    @Positive(message = "allowed requests must be positive")
    private Integer allowedRequests;

    @NotNull(message = "window seconds is required")
    @Positive(message = "window seconds must be positive")
    private Integer windowSeconds;
}
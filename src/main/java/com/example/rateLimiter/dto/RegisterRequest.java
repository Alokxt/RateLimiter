package com.example.rateLimiter.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

@Data
public class RegisterRequest {

    @NotBlank(message="Name is required")
    private  String company;

    @NotBlank(message="Origin is required")
    private String origin;

    @NotNull(message="allowed requests is required")
    @Positive(message = "allowed requests smust  be positive")
    private Integer allowedRequests;

    @NotNull(message="window seconds is required")
    @Positive(message = "window seconds must be positive")
    private Integer defaultWindowSecond;


}

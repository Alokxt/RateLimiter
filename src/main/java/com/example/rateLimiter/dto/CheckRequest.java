package com.example.rateLimiter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckRequest {
    @NotBlank(message = "route is needed")
    private String route;

}

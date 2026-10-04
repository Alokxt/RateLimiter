package com.example.rateLimiter.repository;

import com.example.rateLimiter.entity.EndpointConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EndpointConfigRepo extends JpaRepository<EndpointConfig,Long> {
    List<EndpointConfig> findByBackendServiceId(Long backendServiceId);
    boolean existsByBackendServiceIdAndEndpointPath(Long backendServiceId, String endpointPath);

}

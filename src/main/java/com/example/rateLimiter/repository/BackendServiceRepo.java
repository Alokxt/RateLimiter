package com.example.rateLimiter.repository;

import com.example.rateLimiter.entity.BackendService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackendServiceRepo extends JpaRepository<BackendService,Long> {
    Optional<BackendService> findByKeyHash(String key);
    boolean existsByOrigin(String origin);
    boolean existsByCompany(String company);
}

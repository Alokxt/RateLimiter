package com.example.rateLimiter.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "backend_service")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackendService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String keyHash;

    @Column(nullable = false, unique = true)
    private String company;

    @Column(nullable = false, unique = true)
    private String origin;

    @Column(nullable = false)
    private Integer allowedRequests;

    @Column(nullable = false)
    private Integer defaultWindowSecond;

    @OneToMany(mappedBy = "backendService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EndpointConfig>  endpointConfigs;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant created;



}

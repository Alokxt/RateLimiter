package com.example.rateLimiter.entity;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "endpoint_config",
        uniqueConstraints = @UniqueConstraint(columnNames = {"backend_service_id", "endpoint_path"})
)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backend_service_id", nullable = false)
    private BackendService backendService;

    @Column(nullable = false)
    private String endpointPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlgorithmType algorithmType;

    @Column(nullable = false)
    private Integer allowedRequests;

    @Column(nullable = false)
    private Integer windowSeconds;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

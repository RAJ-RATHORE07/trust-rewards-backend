package com.fintech.audit.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 4000)
    private String payload;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {}

    public AuditLog(
            String eventType,
            String entityType,
            String entityId,
            UUID userId,
            String payload) {
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.payload = payload;
    }
}

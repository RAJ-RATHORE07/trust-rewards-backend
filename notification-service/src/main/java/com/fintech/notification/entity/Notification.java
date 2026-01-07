package com.fintech.notification.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "notifications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"eventId", "type"})
)
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Notification() {}

    public Notification(UUID userId, UUID eventId, String type, String message) {
        this.userId = userId;
        this.eventId = eventId;
        this.type = type;
        this.message = message;
    }
}

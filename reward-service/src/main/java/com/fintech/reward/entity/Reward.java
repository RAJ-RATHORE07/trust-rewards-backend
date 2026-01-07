package com.fintech.reward.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rewards", uniqueConstraints = @UniqueConstraint(columnNames = "paymentId"))
public class Reward {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private UUID paymentId;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Reward() {
    }

    public Reward(UUID userId, UUID paymentId, int points) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.points = points;
    }

    // ✅ REQUIRED GETTERS
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public int getPoints() {
        return points;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

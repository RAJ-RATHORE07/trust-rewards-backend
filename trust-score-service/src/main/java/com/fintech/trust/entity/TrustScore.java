package com.fintech.trust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "trust_scores")
public class TrustScore {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private int score;

    protected TrustScore() {
    }

    public TrustScore(UUID userId, int score) {
        this.userId = userId;
        this.score = score;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

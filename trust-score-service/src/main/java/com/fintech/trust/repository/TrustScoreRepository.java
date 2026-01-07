package com.fintech.trust.repository;

import com.fintech.trust.entity.TrustScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrustScoreRepository extends JpaRepository<TrustScore, UUID> {

    Optional<TrustScore> findByUserId(UUID userId);
}

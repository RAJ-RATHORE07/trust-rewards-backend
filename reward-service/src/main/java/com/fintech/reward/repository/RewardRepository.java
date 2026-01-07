package com.fintech.reward.repository;

import com.fintech.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RewardRepository extends JpaRepository<Reward, UUID> {
    Optional<Reward> findByPaymentId(UUID paymentId);
}

package com.fintech.trust.consumer;

import com.fintech.trust.entity.TrustScore;
import com.fintech.events.PaymentCreatedEvent;
import com.fintech.trust.repository.TrustScoreRepository;
import com.fintech.trust.service.TrustScoreCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class PaymentEventConsumer {

    private final TrustScoreRepository repository;
    private final TrustScoreCalculator calculator;

    public PaymentEventConsumer(
            TrustScoreRepository repository,
            TrustScoreCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @RetryableTopic(attempts = "4", // 1 original + 3 retries
            backoff = @Backoff(delay = 5000, // 5 seconds
                    multiplier = 2.0 // exponential backoff
            ), retryTopicSuffix = ".retry", dltTopicSuffix = ".dlq", autoCreateTopics = "true")
    @KafkaListener(topics = "payment.created", groupId = "trust-score-group")
    @Transactional
    public void handle(PaymentCreatedEvent event) {

        log.info("Processing payment event {}", event.paymentId());

        // 🔥 Simulate failure (for testing retry + DLQ)
        if (event.amount().intValue() > 10000) {
            throw new RuntimeException("Simulated failure for retry");
        }

        UUID userId = event.userId();

        int delta = calculator.calculate(event);

        TrustScore trustScore = repository
                .findByUserId(userId)
                .orElseGet(() -> new TrustScore(userId, 0));

        trustScore.setScore(trustScore.getScore() + delta);
        repository.save(trustScore);

        log.info("✅ Trust score updated for user {}", userId);
    }
}

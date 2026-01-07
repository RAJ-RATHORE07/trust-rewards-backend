package com.fintech.reward.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.events.PaymentCreatedEvent;
import com.fintech.reward.entity.Reward;
import com.fintech.reward.event.RewardEventProducer;
import com.fintech.reward.event.RewardGrantedEvent;
import com.fintech.reward.repository.RewardRepository;
import com.fintech.reward.service.RewardCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PaymentEventConsumer {

    private final RewardRepository repository;
    private final RewardCalculator calculator;
    private final RewardEventProducer producer;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(
            RewardRepository repository,
            RewardCalculator calculator,
            RewardEventProducer producer,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.calculator = calculator;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 3000), autoCreateTopics = "true")
    @KafkaListener(topics = "payment.created", groupId = "reward-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(String message) {

        try {
            PaymentCreatedEvent event = objectMapper.readValue(message, PaymentCreatedEvent.class);

            // ✅ Idempotency
            if (repository.findByPaymentId(event.paymentId()).isPresent()) {
                log.info("Reward already exists for payment {}", event.paymentId());
                return;
            }

            int points = calculator.calculate(event.amount());

            Reward reward = new Reward(
                    event.userId(),
                    event.paymentId(),
                    points);

            repository.save(reward);

            RewardGrantedEvent rewardEvent = new RewardGrantedEvent(
                    reward.getId(),
                    reward.getUserId(),
                    reward.getPoints());

            producer.publish(rewardEvent);

            log.info("🎁 Reward granted → user={} points={}",
                    event.userId(), points);

        } catch (Exception e) {
            log.error("Failed to process payment.created event", e);
            throw new RuntimeException(e); // important for retry/DLQ later
        }
    }
}

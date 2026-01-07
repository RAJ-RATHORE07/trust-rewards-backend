package com.fintech.reward.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RewardDLQListener {

    @KafkaListener(topics = "payment.created-dlt", groupId = "reward-dlq-group")
    public void handleDLQ(String message) {
        log.error("🚨 REWARD DLQ EVENT 🚨 {}", message);
        // save to DB / alert / manual recovery
    }
}

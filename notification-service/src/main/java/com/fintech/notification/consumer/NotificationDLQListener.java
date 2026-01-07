package com.fintech.notification.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDLQListener {

    @KafkaListener(topics = "reward.granted.v1.dlq",
                   groupId = "notification-dlq-group")
    public void handleDLQ(String message) {
        log.error("🚨 NOTIFICATION DLQ EVENT 🚨 {}", message);
        // save to audit table / alert ops
    }
}

package com.fintech.trust.consumer;

import com.fintech.events.PaymentCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentDLQListener {

    @KafkaListener(topics = "payment.created.dlq", groupId = "trust-score-dlq-group")
    public void handleDLQ(PaymentCreatedEvent event) {

        log.error("🚨 DLQ EVENT RECEIVED 🚨");
        log.error("PaymentId={} UserId={} Amount={}",
                event.paymentId(),
                event.userId(),
                event.amount());

        // TODO (later):
        // 1. Save to audit table
        // 2. Alert admin
        // 3. Trigger manual recovery
    }
}

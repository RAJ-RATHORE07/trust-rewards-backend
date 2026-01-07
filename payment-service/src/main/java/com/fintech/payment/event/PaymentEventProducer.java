package com.fintech.payment.event;

import com.fintech.events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String TOPIC = "payment.created";

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    public void publish(PaymentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.paymentId().toString(), event);
    }
}

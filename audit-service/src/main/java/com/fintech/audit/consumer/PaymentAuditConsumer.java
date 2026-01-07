package com.fintech.audit.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.audit.service.AuditWriter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentAuditConsumer {

    private final AuditWriter writer;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentAuditConsumer(AuditWriter writer) {
        this.writer = writer;
    }

    @KafkaListener(topics = "payment.created.v1", groupId = "audit-group")
    public void handle(String message) throws Exception {

        JsonNode json = mapper.readTree(message);

        writer.write(
                "PAYMENT_CREATED",
                "PAYMENT",
                json.get("paymentId").asText(),
                UUID.fromString(json.get("userId").asText()),
                message
        );
    }
}

package com.fintech.audit.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.audit.service.AuditWriter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RewardAuditConsumer {

    private final AuditWriter writer;
    private final ObjectMapper mapper = new ObjectMapper();

    public RewardAuditConsumer(AuditWriter writer) {
        this.writer = writer;
    }

    @KafkaListener(topics = "reward.granted.v1", groupId = "audit-group")
    public void handle(String message) throws Exception {

        JsonNode json = mapper.readTree(message);

        writer.write(
                "REWARD_GRANTED",
                "REWARD",
                json.get("rewardId").asText(),
                UUID.fromString(json.get("userId").asText()),
                message
        );
    }
}

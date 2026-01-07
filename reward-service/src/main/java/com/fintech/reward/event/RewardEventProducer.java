package com.fintech.reward.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RewardEventProducer {

    private static final String TOPIC = "reward.granted";

    private final KafkaTemplate<String, RewardGrantedEvent> kafkaTemplate;

    public RewardEventProducer(KafkaTemplate<String, RewardGrantedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RewardGrantedEvent event) {
        kafkaTemplate.send(TOPIC, event.rewardId().toString(), event);
    }
}

package com.fintech.trust.consumer;

import com.fintech.events.PaymentCreatedEvent;
import com.fintech.trust.entity.TrustScore;
import com.fintech.trust.repository.TrustScoreRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrustScoreConsumer {

    private final TrustScoreRepository repository;

    public TrustScoreConsumer(TrustScoreRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "payment.created", groupId = "trust-score-group", containerFactory = "kafkaListenerContainerFactory")
    public void handlePayment(PaymentCreatedEvent event) {

        TrustScore score = repository.findById(event.userId())
                .orElse(new TrustScore(event.userId(), 0));

        score.setScore(score.getScore() + 10);
        repository.save(score);

        System.out.println("✅ Trust score updated for user " + event.userId());
    }
}

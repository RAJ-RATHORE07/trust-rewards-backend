package com.fintech.notification.consumer;

import com.fintech.notification.entity.Notification;
import com.fintech.notification.event.RewardGrantedEvent;
import com.fintech.notification.repository.NotificationRepository;
import com.fintech.notification.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RewardGrantedConsumer {

    private final NotificationRepository repository;
    private final NotificationSender sender;

    public RewardGrantedConsumer(NotificationRepository repository,
                                 NotificationSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 3000, multiplier = 2),
            dltTopicSuffix = ".dlq",
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics = "reward.granted.v1",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handle(RewardGrantedEvent event) {

        // ✅ Idempotency
        if (repository.existsByEventIdAndType(event.rewardId(), "REWARD")) {
            log.info("Notification already sent for reward {}", event.rewardId());
            return;
        }

        String message =
                "🎉 Congratulations! You earned " + event.points() + " reward points.";

        Notification notification = new Notification(
                event.userId(),
                event.rewardId(),
                "REWARD",
                message
        );

        repository.save(notification);
        sender.send(message);

        log.info("📢 Notification sent → user={} points={}",
                event.userId(), event.points());
    }
}

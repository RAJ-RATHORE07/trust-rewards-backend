package com.fintech.reward.event;

import java.util.UUID;

public record RewardGrantedEvent(
        UUID rewardId,
        UUID userId,
        int points) {
}

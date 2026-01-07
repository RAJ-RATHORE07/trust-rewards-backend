package com.fintech.notification.event;

import java.util.UUID;

public record RewardGrantedEvent(
        UUID rewardId,
        UUID userId,
        int points
) {}

package com.fintech.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID paymentId,
        UUID userId,
        BigDecimal amount,
        String paymentMethod,
        Instant createdAt) {
}

package com.fintech.payment.dto;

import com.fintech.payment.entity.PaymentStatus;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        PaymentStatus status,
        String message) {
}

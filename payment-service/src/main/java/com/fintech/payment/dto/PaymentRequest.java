package com.fintech.payment.dto;

import com.fintech.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull PaymentMethod paymentMethod) {
}

package com.fintech.payment.controller;

import com.fintech.payment.dto.*;
import com.fintech.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();
        return service.createPayment(userId, request, idempotencyKey);
    }
}

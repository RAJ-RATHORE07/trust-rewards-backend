package com.fintech.payment.service;

import com.fintech.payment.dto.PaymentRequest;
import com.fintech.payment.dto.PaymentResponse;
import com.fintech.payment.entity.Payment;
import com.fintech.payment.entity.PaymentStatus;
import com.fintech.events.PaymentCreatedEvent;

import com.fintech.payment.event.PaymentEventProducer;
import com.fintech.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

        private final PaymentRepository repository;
        private final PaymentEventProducer eventProducer;

        public PaymentService(
                        PaymentRepository repository,
                        PaymentEventProducer eventProducer) {
                this.repository = repository;
                this.eventProducer = eventProducer;
        }

        /**
         * Idempotent payment creation
         */
        @Transactional
        public PaymentResponse createPayment(
                        UUID userId,
                        PaymentRequest request,
                        String idempotencyKey) {

                log.info("➡️ Create payment request userId={}, amount={}, key={}",
                                userId, request.amount(), idempotencyKey);

                return repository.findByIdempotencyKey(idempotencyKey)
                                .map(existing -> {
                                        log.warn("⚠️ Duplicate payment detected for key={}", idempotencyKey);
                                        return new PaymentResponse(
                                                        existing.getId(),
                                                        existing.getStatus(),
                                                        "Duplicate request – returning existing payment");
                                })
                                .orElseGet(() -> createNewPayment(userId, request, idempotencyKey));
        }

        /**
         * Creates new payment and publishes Kafka event
         */
        @Transactional
        protected PaymentResponse createNewPayment(
                        UUID userId,
                        PaymentRequest request,
                        String idempotencyKey) {

                try {
                        Payment payment = new Payment();
                        payment.setUserId(userId);
                        payment.setAmount(request.amount());
                        payment.setPaymentMethod(request.paymentMethod());
                        payment.setStatus(PaymentStatus.CREATED);
                        payment.setIdempotencyKey(idempotencyKey);

                        repository.save(payment);

                        log.info("💾 Payment saved id={}", payment.getId());

                        PaymentCreatedEvent event = new PaymentCreatedEvent(
                                        payment.getId(),
                                        payment.getUserId(),
                                        payment.getAmount(),
                                        payment.getPaymentMethod().name(),
                                        payment.getCreatedAt());

                        log.info("🚀 Publishing PaymentCreatedEvent → {}", event);

                        eventProducer.publish(event);

                        return new PaymentResponse(
                                        payment.getId(),
                                        payment.getStatus(),
                                        "Payment created successfully");

                } catch (DataIntegrityViolationException ex) {

                        log.error("❌ DataIntegrityViolation for key={}, recovering", idempotencyKey);

                        Payment existing = repository.findByIdempotencyKey(idempotencyKey)
                                        .orElseThrow();

                        return new PaymentResponse(
                                        existing.getId(),
                                        existing.getStatus(),
                                        "Recovered duplicate request");
                }
        }
}

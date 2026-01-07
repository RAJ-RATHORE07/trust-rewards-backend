package com.fintech.trust.service;

import com.fintech.events.PaymentCreatedEvent;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class TrustScoreCalculator {

    public int calculate(PaymentCreatedEvent event) {

        int score = 10;

        if (event.amount().compareTo(new BigDecimal("5000")) > 0) {
            score += 5;
        }

        if (event.amount().compareTo(new BigDecimal("20000")) > 0) {
            score += 10;
        }

        return score;
    }

}

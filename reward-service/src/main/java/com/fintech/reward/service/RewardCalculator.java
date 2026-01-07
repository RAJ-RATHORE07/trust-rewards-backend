package com.fintech.reward.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RewardCalculator {

    public int calculate(BigDecimal amount) {

        if (amount.intValue() >= 10000) return 100;
        if (amount.intValue() >= 5000) return 50;
        if (amount.intValue() >= 1000) return 10;

        return 1;
    }
}

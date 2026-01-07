package com.fintech.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationSender {

    public void send(String message) {
        // email / SMS / push (mock)
        log.info("📨 Sending notification → {}", message);
    }
}

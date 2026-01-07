package com.fintech.audit.service;

import com.fintech.audit.entity.AuditLog;
import com.fintech.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditWriter {

    private final AuditLogRepository repository;

    public AuditWriter(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void write(
            String eventType,
            String entityType,
            String entityId,
            UUID userId,
            String payload) {

        AuditLog log = new AuditLog(
                eventType,
                entityType,
                entityId,
                userId,
                payload
        );

        repository.save(log);
    }
}

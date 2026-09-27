package com.louky.bank.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AuditLogResponse {

    private final Long id;
    private final Long userId;
    private final String userEmail;
    private final String userDisplay;
    private final String action;
    private final String entityType;
    private final Long entityId;
    private final String details;
    private final String ipAddress;
    private final Instant createdAt;
}
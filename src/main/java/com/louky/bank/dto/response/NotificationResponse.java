package com.louky.bank.dto.response;

import com.louky.bank.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class NotificationResponse {

    private final Long id;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final boolean read;
    private final Long referenceId;
    private final Instant createdAt;
}
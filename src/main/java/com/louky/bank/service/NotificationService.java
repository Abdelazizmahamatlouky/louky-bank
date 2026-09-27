package com.louky.bank.service;

import com.louky.bank.domain.enums.NotificationType;
import com.louky.bank.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void notifyUser(Long userId, String title, String message, NotificationType type, Long referenceId);

    Page<NotificationResponse> findForCurrentUser(Pageable pageable);

    long countUnread();

    void markAsRead(Long notificationId);

    void markAllAsRead();
}
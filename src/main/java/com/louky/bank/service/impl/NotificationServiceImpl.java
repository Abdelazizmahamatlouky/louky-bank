package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.Notification;
import com.louky.bank.domain.entity.User;
import com.louky.bank.domain.enums.NotificationType;
import com.louky.bank.dto.response.NotificationResponse;
import com.louky.bank.exception.ResourceNotFoundException;
import com.louky.bank.mapper.NotificationMapper;
import com.louky.bank.repository.NotificationRepository;
import com.louky.bank.repository.UserRepository;
import com.louky.bank.service.NotificationService;
import com.louky.bank.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void notifyUser(Long userId, String title, String message, NotificationType type, Long referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable: " + userId));
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findForCurrentUser(Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        return notificationRepository.countByUserIdAndReadFalse(SecurityUtils.currentUserId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable: " + notificationId));
        if (!notification.getUser().getId().equals(SecurityUtils.currentUserId())) {
            throw new ResourceNotFoundException("Notification introuvable: " + notificationId);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtils.currentUserId();
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }
}
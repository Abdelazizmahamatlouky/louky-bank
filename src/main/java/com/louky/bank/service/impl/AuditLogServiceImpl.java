package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.AuditLog;
import com.louky.bank.dto.response.AuditLogResponse;
import com.louky.bank.mapper.AuditLogMapper;
import com.louky.bank.repository.AuditLogRepository;
import com.louky.bank.security.SecurityUser;
import com.louky.bank.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void log(String action, String entityType, Long entityId, String details) {
        Long userId = null;
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            userId = securityUser.getId();
            userEmail = securityUser.getUsername();
        }
        persist(userId, userEmail, action, entityType, entityId, details);
    }

    @Override
    @Transactional
    public void logAsUser(Long userId, String userEmail,
                          String action, String entityType, Long entityId, String details) {
        // Si l'appelant ne connait pas l'utilisateur, on retombe sur le contexte
        if (userId == null && userEmail == null) {
            log(action, entityType, entityId, details);
            return;
        }
        persist(userId, userEmail, action, entityType, entityId, details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(auditLogMapper::toResponse);
    }

    private void persist(Long userId, String userEmail,
                         String action, String entityType, Long entityId, String details) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .userEmail(userEmail)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
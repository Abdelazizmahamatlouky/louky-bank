package com.louky.bank.mapper;

import com.louky.bank.domain.entity.AuditLog;
import com.louky.bank.dto.response.AuditLogResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface AuditLogMapper {

    @Mapping(target  = "userDisplay", expression = "java(buildUserDisplay(auditLog))")
    AuditLogResponse toResponse(AuditLog auditLog);

    default String buildUserDisplay(AuditLog log) {
        if (log == null) return null;
        if (log.getUserEmail() != null && !log.getUserEmail().isBlank()) {
            return log.getUserEmail();
        }
        if (log.getUserId() != null) {
            return "Utilisateur #" + log.getUserId();
        }
        return null;
    }
}
package com.louky.bank.controller;

import com.louky.bank.dto.response.AuditLogResponse;
import com.louky.bank.dto.response.PageResponse;
import com.louky.bank.service.AuditLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Journal d'audit")
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DIRECTEUR')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<PageResponse<AuditLogResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(auditLogService.findAll(pageable)));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PageResponse<AuditLogResponse>> findByUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(auditLogService.findByUser(userId, pageable)));
    }
}
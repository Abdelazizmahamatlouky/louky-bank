package com.louky.bank.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user", columnList = "user_id"),
        @Index(name = "idx_audit_action", columnList = "action")
})
public class AuditLog extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", length = 150)
    private String userEmail;

    @Column(name = "action", nullable = false, length = 80)
    private String action;

    @Column(name = "entity_type", length = 80)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "details", length = 2000)
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
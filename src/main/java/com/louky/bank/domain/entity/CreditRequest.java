package com.louky.bank.domain.entity;

import com.louky.bank.domain.enums.CreditStatus;
import com.louky.bank.domain.enums.CreditType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_credit_reference", columnNames = "reference")
        },
        indexes = {
                @Index(name = "idx_credit_status", columnList = "status"),
                @Index(name = "idx_credit_agency", columnList = "agency_id"),
                @Index(name = "idx_credit_created_at", columnList = "created_at")
        })
public class CreditRequest extends BaseEntity {

    @Column(name = "reference", nullable = false, length = 30)
    private String reference;

    @Column(name = "applicant_first_name", nullable = false, length = 80)
    private String applicantFirstName;

    @Column(name = "applicant_last_name", nullable = false, length = 80)
    private String applicantLastName;

    @Column(name = "applicant_email", length = 150)
    private String applicantEmail;

    @Column(name = "applicant_phone", length = 30)
    private String applicantPhone;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "monthly_income", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_charges", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyCharges;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false, length = 30)
    private CreditType creditType;

    @Column(name = "purpose", nullable = false, length = 500)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CreditStatus status = CreditStatus.BROUILLON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToOne(mappedBy = "creditRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CreditAssessment assessment;
}
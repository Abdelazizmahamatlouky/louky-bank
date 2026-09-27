package com.louky.bank.domain.entity;

import com.louky.bank.domain.enums.RiskCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "credit_assessments")
public class CreditAssessment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_request_id", nullable = false, unique = true)
    private CreditRequest creditRequest;

    @Column(name = "debt_ratio", nullable = false, precision = 7, scale = 2)
    private BigDecimal debtRatio;

    @Column(name = "monthly_payment", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "total_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false, length = 20)
    private RiskCategory riskCategory;

    @Column(name = "applied_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal appliedRate;
}
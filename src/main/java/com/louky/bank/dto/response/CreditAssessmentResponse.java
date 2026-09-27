package com.louky.bank.dto.response;

import com.louky.bank.domain.enums.RiskCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreditAssessmentResponse {

    private final Long id;
    private final BigDecimal debtRatio;
    private final BigDecimal monthlyPayment;
    private final BigDecimal totalCost;
    private final Integer riskScore;
    private final RiskCategory riskCategory;
    private final BigDecimal appliedRate;
}
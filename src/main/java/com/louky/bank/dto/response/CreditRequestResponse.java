package com.louky.bank.dto.response;

import com.louky.bank.domain.enums.CreditStatus;
import com.louky.bank.domain.enums.CreditType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class CreditRequestResponse {

    private final Long id;
    private final String reference;
    private final String applicantFirstName;
    private final String applicantLastName;
    private final String applicantEmail;
    private final String applicantPhone;
    private final BigDecimal amount;
    private final Integer durationMonths;
    private final BigDecimal monthlyIncome;
    private final BigDecimal monthlyCharges;
    private final CreditType creditType;
    private final String purpose;
    private final CreditStatus status;
    private final Long agencyId;
    private final String agencyName;
    private final Long createdById;
    private final String createdByName;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final CreditAssessmentResponse assessment;
}
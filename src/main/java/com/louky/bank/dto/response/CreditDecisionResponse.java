package com.louky.bank.dto.response;

import com.louky.bank.domain.enums.DecisionType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CreditDecisionResponse {

    private final Long id;
    private final Long creditRequestId;
    private final String creditReference;
    private final Long decisionMakerId;
    private final String decisionMakerName;
    private final DecisionType decision;
    private final String comment;
    private final Instant createdAt;
}
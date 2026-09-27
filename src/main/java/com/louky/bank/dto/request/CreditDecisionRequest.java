package com.louky.bank.dto.request;

import com.louky.bank.domain.enums.DecisionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditDecisionRequest {

    @NotNull
    private DecisionType decision;

    @Size(max = 1000)
    private String comment;
}
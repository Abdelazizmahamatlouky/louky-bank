package com.louky.bank.mapper;

import com.louky.bank.domain.entity.CreditDecision;
import com.louky.bank.dto.response.CreditDecisionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditDecisionMapper {

    @Mapping(target = "creditRequestId", source = "creditRequest.id")
    @Mapping(target = "creditReference", source = "creditRequest.reference")
    @Mapping(target = "decisionMakerId", source = "decisionMaker.id")
    @Mapping(target = "decisionMakerName", expression = "java(decision.getDecisionMaker().getFirstName() + \" \" + decision.getDecisionMaker().getLastName())")
    CreditDecisionResponse toResponse(CreditDecision decision);
}
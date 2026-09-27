package com.louky.bank.mapper;

import com.louky.bank.domain.entity.CreditAssessment;
import com.louky.bank.dto.response.CreditAssessmentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditAssessmentMapper {

    CreditAssessmentResponse toResponse(CreditAssessment assessment);
}
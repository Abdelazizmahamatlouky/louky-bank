package com.louky.bank.service;

import com.louky.bank.domain.entity.CreditAssessment;
import com.louky.bank.domain.entity.CreditRequest;
import com.louky.bank.dto.response.CreditAssessmentResponse;

public interface CreditAssessmentService {

    CreditAssessment evaluate(CreditRequest request);

    CreditAssessmentResponse findByCreditRequestId(Long creditRequestId);
}
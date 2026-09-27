package com.louky.bank.service;

import com.louky.bank.dto.response.CreditDecisionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditDecisionService {

    Page<CreditDecisionResponse> findByCreditRequest(Long creditRequestId, Pageable pageable);
}
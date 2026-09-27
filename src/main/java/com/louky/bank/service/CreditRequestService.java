package com.louky.bank.service;

import com.louky.bank.dto.request.CreditDecisionRequest;
import com.louky.bank.dto.request.CreditRequestCreateRequest;
import com.louky.bank.dto.request.CreditRequestFilter;
import com.louky.bank.dto.request.CreditRequestUpdateRequest;
import com.louky.bank.dto.response.CreditDecisionResponse;
import com.louky.bank.dto.response.CreditRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CreditRequestService {

    CreditRequestResponse create(CreditRequestCreateRequest request);

    CreditRequestResponse update(Long id, CreditRequestUpdateRequest request);

    CreditRequestResponse findById(Long id);

    Page<CreditRequestResponse> search(CreditRequestFilter filter, Pageable pageable);

    CreditRequestResponse submit(Long id);

    CreditRequestResponse decide(Long id, CreditDecisionRequest request);

    CreditRequestResponse cancel(Long id);

    List<CreditDecisionResponse> history(Long id);
}
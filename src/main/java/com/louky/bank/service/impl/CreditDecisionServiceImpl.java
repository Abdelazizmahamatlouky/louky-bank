package com.louky.bank.service.impl;

import com.louky.bank.dto.response.CreditDecisionResponse;
import com.louky.bank.mapper.CreditDecisionMapper;
import com.louky.bank.repository.CreditDecisionRepository;
import com.louky.bank.service.CreditDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditDecisionServiceImpl implements CreditDecisionService {

    private final CreditDecisionRepository creditDecisionRepository;
    private final CreditDecisionMapper creditDecisionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CreditDecisionResponse> findByCreditRequest(Long creditRequestId, Pageable pageable) {
        return creditDecisionRepository.findByCreditRequestId(creditRequestId, pageable)
                .map(creditDecisionMapper::toResponse);
    }
}
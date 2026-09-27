package com.louky.bank.service;

import com.louky.bank.dto.request.AgencyCreateRequest;
import com.louky.bank.dto.request.AgencyUpdateRequest;
import com.louky.bank.dto.response.AgencyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgencyService {

    AgencyResponse create(AgencyCreateRequest request);

    AgencyResponse update(Long id, AgencyUpdateRequest request);

    AgencyResponse findById(Long id);

    Page<AgencyResponse> findAll(Pageable pageable);
}
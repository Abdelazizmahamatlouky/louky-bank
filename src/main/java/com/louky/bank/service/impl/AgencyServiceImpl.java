package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.Agency;
import com.louky.bank.dto.request.AgencyCreateRequest;
import com.louky.bank.dto.request.AgencyUpdateRequest;
import com.louky.bank.dto.response.AgencyResponse;
import com.louky.bank.exception.DuplicateResourceException;
import com.louky.bank.exception.ResourceNotFoundException;
import com.louky.bank.mapper.AgencyMapper;
import com.louky.bank.repository.AgencyRepository;
import com.louky.bank.service.AgencyService;
import com.louky.bank.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyMapper agencyMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AgencyResponse create(AgencyCreateRequest request) {
        if (agencyRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Une agence avec ce code existe deja");
        }
        Agency agency = Agency.builder()
                .code(request.getCode())
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .phone(request.getPhone())
                .build();
        Agency saved = agencyRepository.save(agency);
        auditLogService.log("CREATE_AGENCY", "Agency", saved.getId(), "Creation agence " + saved.getCode());
        return agencyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AgencyResponse update(Long id, AgencyUpdateRequest request) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable: " + id));
        if (request.getName() != null) {
            agency.setName(request.getName());
        }
        if (request.getCity() != null) {
            agency.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            agency.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            agency.setPhone(request.getPhone());
        }
        if (request.getActive() != null) {
            agency.setActive(request.getActive());
        }
        Agency saved = agencyRepository.save(agency);
        auditLogService.log("UPDATE_AGENCY", "Agency", saved.getId(), "Mise a jour agence " + saved.getCode());
        return agencyMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AgencyResponse findById(Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable: " + id));
        return agencyMapper.toResponse(agency);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgencyResponse> findAll(Pageable pageable) {
        return agencyRepository.findAll(pageable).map(agencyMapper::toResponse);
    }
}
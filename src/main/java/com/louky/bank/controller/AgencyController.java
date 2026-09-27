package com.louky.bank.controller;

import com.louky.bank.dto.request.AgencyCreateRequest;
import com.louky.bank.dto.request.AgencyUpdateRequest;
import com.louky.bank.dto.response.AgencyResponse;
import com.louky.bank.dto.response.PageResponse;
import com.louky.bank.service.AgencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agences")
@RestController
@RequestMapping("/api/v1/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<AgencyResponse> create(@Valid @RequestBody AgencyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agencyService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<AgencyResponse> update(@PathVariable Long id, @Valid @RequestBody AgencyUpdateRequest request) {
        return ResponseEntity.ok(agencyService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agencyService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AgencyResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(agencyService.findAll(pageable)));
    }
}
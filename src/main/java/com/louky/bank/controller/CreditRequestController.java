package com.louky.bank.controller;

import com.louky.bank.dto.request.CreditDecisionRequest;
import com.louky.bank.dto.request.CreditRequestCreateRequest;
import com.louky.bank.dto.request.CreditRequestFilter;
import com.louky.bank.dto.request.CreditRequestUpdateRequest;
import com.louky.bank.dto.response.CreditDecisionResponse;
import com.louky.bank.dto.response.CreditRequestResponse;
import com.louky.bank.dto.response.PageResponse;
import com.louky.bank.service.CreditRequestService;
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

import java.util.List;

@Tag(name = "Demandes de credit")
@RestController
@RequestMapping("/api/v1/credit-requests")
@RequiredArgsConstructor
public class CreditRequestController {

    private final CreditRequestService creditRequestService;

    @PostMapping
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CreditRequestResponse> create(@Valid @RequestBody CreditRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditRequestService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CreditRequestResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody CreditRequestUpdateRequest request) {
        return ResponseEntity.ok(creditRequestService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditRequestResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(creditRequestService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CreditRequestResponse>> search(CreditRequestFilter filter, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(creditRequestService.search(filter, pageable)));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CreditRequestResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(creditRequestService.submit(id));
    }

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<CreditRequestResponse> decide(@PathVariable Long id,
                                                        @Valid @RequestBody CreditDecisionRequest request) {
        return ResponseEntity.ok(creditRequestService.decide(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CreditRequestResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(creditRequestService.cancel(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CreditDecisionResponse>> history(@PathVariable Long id) {
        return ResponseEntity.ok(creditRequestService.history(id));
    }
}
package com.louky.bank.controller;

import com.louky.bank.dto.response.CreditDecisionResponse;
import com.louky.bank.dto.response.PageResponse;
import com.louky.bank.service.CreditDecisionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Decisions de credit")
@RestController
@RequestMapping("/api/v1/credit-decisions")
@RequiredArgsConstructor
public class CreditDecisionController {

    private final CreditDecisionService creditDecisionService;

    @GetMapping("/by-request/{creditRequestId}")
    public ResponseEntity<PageResponse<CreditDecisionResponse>> findByCreditRequest(@PathVariable Long creditRequestId,
                                                                                    Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(
                creditDecisionService.findByCreditRequest(creditRequestId, pageable)));
    }
}
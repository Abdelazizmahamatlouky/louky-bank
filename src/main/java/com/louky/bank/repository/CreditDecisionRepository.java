package com.louky.bank.repository;

import com.louky.bank.domain.entity.CreditDecision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditDecisionRepository extends JpaRepository<CreditDecision, Long> {

    List<CreditDecision> findByCreditRequestIdOrderByCreatedAtAsc(Long creditRequestId);

    Page<CreditDecision> findByCreditRequestId(Long creditRequestId, Pageable pageable);
}
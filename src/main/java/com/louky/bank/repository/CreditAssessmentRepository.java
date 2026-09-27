package com.louky.bank.repository;

import com.louky.bank.domain.entity.CreditAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditAssessmentRepository extends JpaRepository<CreditAssessment, Long> {

    Optional<CreditAssessment> findByCreditRequestId(Long creditRequestId);
}
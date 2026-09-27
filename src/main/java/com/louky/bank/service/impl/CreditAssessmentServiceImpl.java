package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.CreditAssessment;
import com.louky.bank.domain.entity.CreditRequest;
import com.louky.bank.domain.enums.RiskCategory;
import com.louky.bank.dto.response.CreditAssessmentResponse;
import com.louky.bank.exception.ResourceNotFoundException;
import com.louky.bank.mapper.CreditAssessmentMapper;
import com.louky.bank.repository.CreditAssessmentRepository;
import com.louky.bank.service.CreditAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Service de calcul financier demonstratif.
 *
 * ATTENTION : le score de risque produit ici est un indicateur pedagogique
 * et ne constitue en aucun cas une decision bancaire reglementaire.
 * Aucune approbation automatique ne doit etre basee sur ce score seul.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditAssessmentServiceImpl implements CreditAssessmentService {

    /** Taux annuel de base applique pour la demonstration. */
    private static final BigDecimal BASE_ANNUAL_RATE = new BigDecimal("0.075");

    /** Marge maximale appliquee selon le profil. */
    private static final BigDecimal MAX_MARGIN = new BigDecimal("0.045");

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);

    private final CreditAssessmentRepository creditAssessmentRepository;
    private final CreditAssessmentMapper creditAssessmentMapper;

    /**
     * Evalue une demande de credit et retourne une evaluation financiere.
     * Le calcul est centralise et documente : taux d'endettement, mensualite,
     * cout total, score et categorie de risque.
     */
    @Override
    @Transactional
    public CreditAssessment evaluate(CreditRequest request) {
        BigDecimal debtRatio = computeDebtRatio(request);
        BigDecimal annualRate = determineAnnualRate(debtRatio);
        BigDecimal monthlyPayment = computeMonthlyPayment(request.getAmount(), annualRate, request.getDurationMonths());
        BigDecimal totalCost = monthlyPayment.multiply(BigDecimal.valueOf(request.getDurationMonths()), MATH_CONTEXT);
        int riskScore = computeRiskScore(debtRatio, request.getAmount(), request.getMonthlyIncome(), request.getDurationMonths());
        RiskCategory category = categorise(riskScore);

        CreditAssessment assessment = request.getAssessment();
        if (assessment == null) {
            assessment = CreditAssessment.builder()
                    .creditRequest(request)
                    .build();
        }
        assessment.setDebtRatio(debtRatio);
        assessment.setAppliedRate(annualRate.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP));
        assessment.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
        assessment.setTotalCost(totalCost.setScale(2, RoundingMode.HALF_UP));
        assessment.setRiskScore(riskScore);
        assessment.setRiskCategory(category);
        CreditAssessment saved = creditAssessmentRepository.save(assessment);
        log.info("Evaluation credit {} : ratio={}%, score={}, categorie={}",
                request.getReference(), debtRatio, riskScore, category);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditAssessmentResponse findByCreditRequestId(Long creditRequestId) {
        CreditAssessment assessment = creditAssessmentRepository.findByCreditRequestId(creditRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation introuvable pour la demande: " + creditRequestId));
        return creditAssessmentMapper.toResponse(assessment);
    }

    private BigDecimal computeDebtRatio(CreditRequest request) {
        if (request.getMonthlyIncome() == null || request.getMonthlyIncome().compareTo(BigDecimal.ZERO) == 0) {
            return HUNDRED;
        }
        return request.getMonthlyCharges()
                .divide(request.getMonthlyIncome(), MATH_CONTEXT)
                .multiply(HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal determineAnnualRate(BigDecimal debtRatio) {
        BigDecimal margin;
        if (debtRatio.compareTo(new BigDecimal("30")) < 0) {
            margin = new BigDecimal("0.010");
        } else if (debtRatio.compareTo(new BigDecimal("50")) < 0) {
            margin = new BigDecimal("0.025");
        } else {
            margin = MAX_MARGIN;
        }
        return BASE_ANNUAL_RATE.add(margin).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal computeMonthlyPayment(BigDecimal amount, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(TWELVE, MATH_CONTEXT);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(months), MATH_CONTEXT);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(onePlusR.pow(months, MATH_CONTEXT), MATH_CONTEXT), MATH_CONTEXT);
        return amount.multiply(monthlyRate, MATH_CONTEXT).divide(denominator, MATH_CONTEXT);
    }

    private int computeRiskScore(BigDecimal debtRatio, BigDecimal amount, BigDecimal monthlyIncome, int months) {
        int score = 100;

        if (debtRatio.compareTo(new BigDecimal("30")) > 0) {
            score -= 20;
        }
        if (debtRatio.compareTo(new BigDecimal("45")) > 0) {
            score -= 20;
        }
        if (debtRatio.compareTo(new BigDecimal("60")) > 0) {
            score -= 20;
        }

        if (monthlyIncome != null && monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = amount.divide(monthlyIncome, MATH_CONTEXT);
            if (ratio.compareTo(BigDecimal.valueOf(24)) > 0) {
                score -= 15;
            }
            if (ratio.compareTo(BigDecimal.valueOf(48)) > 0) {
                score -= 15;
            }
        }

        if (months > 84) {
            score -= 10;
        }

        return Math.max(0, Math.min(100, score));
    }

    private RiskCategory categorise(int score) {
        if (score >= 75) {
            return RiskCategory.FAIBLE;
        }
        if (score >= 50) {
            return RiskCategory.MODERE;
        }
        return RiskCategory.ELEVE;
    }
}
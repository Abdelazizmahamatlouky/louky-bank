package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.CreditDecision;
import com.louky.bank.domain.entity.CreditRequest;
import com.louky.bank.domain.entity.User;
import com.louky.bank.domain.enums.CreditStatus;
import com.louky.bank.domain.enums.DecisionType;
import com.louky.bank.domain.enums.NotificationType;
import com.louky.bank.dto.request.CreditDecisionRequest;
import com.louky.bank.dto.request.CreditRequestCreateRequest;
import com.louky.bank.dto.request.CreditRequestFilter;
import com.louky.bank.dto.request.CreditRequestUpdateRequest;
import com.louky.bank.dto.response.CreditDecisionResponse;
import com.louky.bank.dto.response.CreditRequestResponse;
import com.louky.bank.exception.ForbiddenException;
import com.louky.bank.exception.InvalidStateException;
import com.louky.bank.exception.ResourceNotFoundException;
import com.louky.bank.mapper.CreditDecisionMapper;
import com.louky.bank.mapper.CreditRequestMapper;
import com.louky.bank.repository.CreditDecisionRepository;
import com.louky.bank.repository.CreditRequestRepository;
import com.louky.bank.repository.UserRepository;
import com.louky.bank.security.SecurityUser;
import com.louky.bank.service.AuditLogService;
import com.louky.bank.service.CreditAssessmentService;
import com.louky.bank.service.CreditRequestService;
import com.louky.bank.service.NotificationService;
import com.louky.bank.util.CreditReferenceGenerator;
import com.louky.bank.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditRequestServiceImpl implements CreditRequestService {

    private final CreditRequestRepository creditRequestRepository;
    private final CreditDecisionRepository creditDecisionRepository;
    private final UserRepository userRepository;
    private final CreditRequestMapper creditRequestMapper;
    private final CreditDecisionMapper creditDecisionMapper;
    private final CreditAssessmentService creditAssessmentService;
    private final CreditReferenceGenerator referenceGenerator;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CreditRequestResponse create(CreditRequestCreateRequest request) {
        SecurityUser current = SecurityUtils.currentUser();
        User user = userRepository.findById(current.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        CreditRequest entity = CreditRequest.builder()
                .reference(referenceGenerator.generate())
                .applicantFirstName(request.getApplicantFirstName())
                .applicantLastName(request.getApplicantLastName())
                .applicantEmail(request.getApplicantEmail())
                .applicantPhone(request.getApplicantPhone())
                .amount(request.getAmount())
                .durationMonths(request.getDurationMonths())
                .monthlyIncome(request.getMonthlyIncome())
                .monthlyCharges(request.getMonthlyCharges())
                .creditType(request.getCreditType())
                .purpose(request.getPurpose())
                .status(CreditStatus.BROUILLON)
                .agency(user.getAgency())
                .createdBy(user)
                .build();

        CreditRequest saved = creditRequestRepository.save(entity);
        auditLogService.logAsUser(user.getId(), user.getEmail(),
                "CREATE_CREDIT_REQUEST", "CreditRequest", saved.getId(),
                "Creation demande " + saved.getReference());
        return creditRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CreditRequestResponse update(Long id, CreditRequestUpdateRequest request) {
        CreditRequest entity = loadAuthorized(id);
        if (entity.getStatus() != CreditStatus.BROUILLON) {
            throw new InvalidStateException("Seules les demandes en brouillon peuvent etre modifiees");
        }
        if (request.getApplicantFirstName() != null) {
            entity.setApplicantFirstName(request.getApplicantFirstName());
        }
        if (request.getApplicantLastName() != null) {
            entity.setApplicantLastName(request.getApplicantLastName());
        }
        if (request.getApplicantEmail() != null) {
            entity.setApplicantEmail(request.getApplicantEmail());
        }
        if (request.getApplicantPhone() != null) {
            entity.setApplicantPhone(request.getApplicantPhone());
        }
        if (request.getAmount() != null) {
            entity.setAmount(request.getAmount());
        }
        if (request.getDurationMonths() != null) {
            entity.setDurationMonths(request.getDurationMonths());
        }
        if (request.getMonthlyIncome() != null) {
            entity.setMonthlyIncome(request.getMonthlyIncome());
        }
        if (request.getMonthlyCharges() != null) {
            entity.setMonthlyCharges(request.getMonthlyCharges());
        }
        if (request.getCreditType() != null) {
            entity.setCreditType(request.getCreditType());
        }
        if (request.getPurpose() != null) {
            entity.setPurpose(request.getPurpose());
        }
        CreditRequest saved = creditRequestRepository.save(entity);
        auditLogService.logAsUser(saved.getCreatedBy().getId(), saved.getCreatedBy().getEmail(),
                "UPDATE_CREDIT_REQUEST", "CreditRequest", saved.getId(),
                "Mise a jour demande " + saved.getReference());
        return creditRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditRequestResponse findById(Long id) {
        return creditRequestMapper.toResponse(loadAuthorized(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditRequestResponse> search(CreditRequestFilter filter, Pageable pageable) {
        SecurityUser current = SecurityUtils.currentUser();
        Long agencyId = current.getAgencyId();
        if (filter.getAgencyId() != null) {
            if (!SecurityUtils.isDirecteur() && !filter.getAgencyId().equals(agencyId)) {
                throw new ForbiddenException("Acces inter-agence interdit");
            }
            agencyId = filter.getAgencyId();
        }
        Instant from = filter.getFrom() != null
                ? filter.getFrom().atStartOfDay().toInstant(ZoneOffset.UTC) : null;
        Instant to = filter.getTo() != null
                ? filter.getTo().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC) : null;
        return creditRequestRepository.search(filter.getStatus(), agencyId, filter.getMinAmount(),
                        filter.getMaxAmount(), from, to, pageable)
                .map(creditRequestMapper::toResponse);
    }

    @Override
    @Transactional
    public CreditRequestResponse submit(Long id) {
        CreditRequest entity = loadAuthorized(id);
        if (!SecurityUtils.isAgent() && !SecurityUtils.isDirecteur()) {
            throw new ForbiddenException("Seul un agent peut soumettre une demande");
        }
        if (entity.getStatus() != CreditStatus.BROUILLON) {
            throw new InvalidStateException("Seule une demande en brouillon peut etre soumise");
        }
        entity.setStatus(CreditStatus.SOUMISE);
        creditAssessmentService.evaluate(entity);
        CreditRequest saved = creditRequestRepository.save(entity);
        User author = saved.getCreatedBy();
        auditLogService.logAsUser(author.getId(), author.getEmail(),
                "SUBMIT_CREDIT_REQUEST", "CreditRequest", saved.getId(),
                "Soumission demande " + saved.getReference());
        notifyAgencyDirectors(saved, "Nouvelle demande soumise",
                "La demande " + saved.getReference() + " a ete soumise pour analyse.",
                NotificationType.CREDIT_SOUMIS);
        return creditRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CreditRequestResponse decide(Long id, CreditDecisionRequest request) {
        if (!SecurityUtils.isDirecteur()) {
            throw new ForbiddenException("Seul un directeur peut decider");
        }
        CreditRequest entity = loadAuthorized(id);
        if (entity.getStatus() != CreditStatus.SOUMISE && entity.getStatus() != CreditStatus.EN_ANALYSE) {
            throw new InvalidStateException("Cette demande ne peut pas etre decidee dans son etat actuel");
        }
        if (request.getDecision() == DecisionType.REJETER
                && (request.getComment() == null || request.getComment().isBlank())) {
            throw new InvalidStateException("Une justification est obligatoire en cas de rejet");
        }
        User director = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Directeur introuvable"));

        CreditDecision decision = CreditDecision.builder()
                .creditRequest(entity)
                .decisionMaker(director)
                .decision(request.getDecision())
                .comment(request.getComment())
                .build();
        creditDecisionRepository.save(decision);

        entity.setStatus(request.getDecision() == DecisionType.APPROUVER
                ? CreditStatus.APPROUVEE : CreditStatus.REJETEE);
        CreditRequest saved = creditRequestRepository.save(entity);

        // Audit : on force l'utilisateur (directeur) car on est dans une requete HTTP
        auditLogService.logAsUser(director.getId(), director.getEmail(),
                "DECIDE_CREDIT_REQUEST", "CreditRequest", saved.getId(),
                "Decision " + request.getDecision() + " sur " + saved.getReference());

        NotificationType type = request.getDecision() == DecisionType.APPROUVER
                ? NotificationType.CREDIT_APPROUVE : NotificationType.CREDIT_REJETE;
        notificationService.notifyUser(saved.getCreatedBy().getId(),
                "Decision sur la demande " + saved.getReference(),
                "Votre demande a ete "
                        + (request.getDecision() == DecisionType.APPROUVER ? "approuvee" : "rejetee")
                        + ".",
                type, saved.getId());

        return creditRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CreditRequestResponse cancel(Long id) {
        CreditRequest entity = loadAuthorized(id);
        if (entity.getStatus() == CreditStatus.APPROUVEE || entity.getStatus() == CreditStatus.REJETEE) {
            throw new InvalidStateException("Une demande deja decidee ne peut pas etre annulee");
        }
        if (!entity.getCreatedBy().getId().equals(SecurityUtils.currentUserId()) && !SecurityUtils.isDirecteur()) {
            throw new ForbiddenException("Vous ne pouvez pas annuler cette demande");
        }
        entity.setStatus(CreditStatus.ANNULEE);
        CreditRequest saved = creditRequestRepository.save(entity);

        SecurityUser current = SecurityUtils.currentUser();
        User actor = userRepository.findById(current.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        auditLogService.logAsUser(actor.getId(), actor.getEmail(),
                "CANCEL_CREDIT_REQUEST", "CreditRequest", saved.getId(),
                "Annulation demande " + saved.getReference());
        return creditRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditDecisionResponse> history(Long id) {
        loadAuthorized(id);
        return creditDecisionRepository.findByCreditRequestIdOrderByCreatedAtAsc(id).stream()
                .map(creditDecisionMapper::toResponse)
                .toList();
    }

    private CreditRequest loadAuthorized(Long id) {
        CreditRequest entity = creditRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable: " + id));
        SecurityUser current = SecurityUtils.currentUser();
        if (SecurityUtils.isAgent()) {
            if (entity.getCreatedBy().getId().equals(current.getId())) {
                return entity;
            }
            if (current.getAgencyId() != null && entity.getAgency().getId().equals(current.getAgencyId())) {
                return entity;
            }
            throw new ForbiddenException("Acces inter-agence interdit");
        }
        if (SecurityUtils.isDirecteur() && current.getAgencyId() != null
                && entity.getAgency().getId().equals(current.getAgencyId())) {
            return entity;
        }
        throw new ForbiddenException("Acces refuse a cette demande");
    }

    private void notifyAgencyDirectors(CreditRequest request, String title, String message, NotificationType type) {
        userRepository.findAll().stream()
                .filter(u -> u.getAgency() != null
                        && u.getAgency().getId().equals(request.getAgency().getId())
                        && "DIRECTEUR".equals(u.getRole().name()))
                .forEach(u -> notificationService.notifyUser(u.getId(), title, message, type, request.getId()));
    }
}
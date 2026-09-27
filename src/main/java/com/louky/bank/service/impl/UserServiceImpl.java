package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.Agency;
import com.louky.bank.domain.entity.User;
import com.louky.bank.dto.request.ResetPasswordRequest;
import com.louky.bank.dto.request.UserCreateRequest;
import com.louky.bank.dto.request.UserUpdateRequest;
import com.louky.bank.dto.response.ResetPasswordResponse;
import com.louky.bank.dto.response.UserResponse;
import com.louky.bank.exception.DuplicateResourceException;
import com.louky.bank.exception.ForbiddenException;
import com.louky.bank.exception.InvalidStateException;
import com.louky.bank.exception.ResourceNotFoundException;
import com.louky.bank.mapper.UserMapper;
import com.louky.bank.repository.AgencyRepository;
import com.louky.bank.repository.UserRepository;
import com.louky.bank.security.SecurityUser;
import com.louky.bank.service.AuditLogService;
import com.louky.bank.service.UserService;
import com.louky.bank.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PWD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private static final int PWD_LENGTH = 12;

    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    // ============================================================
    // CREATE
    // ============================================================
    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Un utilisateur avec cet email existe deja");
        }

        SecurityUser current = SecurityUtils.currentUser();
        if (SecurityUtils.isDirecteur()) {
            if (current.getAgencyId() == null
                    || !current.getAgencyId().equals(request.getAgencyId())) {
                throw new ForbiddenException(
                        "Vous ne pouvez creer que des utilisateurs de votre agence");
            }
        }

        Agency agency = agencyRepository.findById(request.getAgencyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agence introuvable: " + request.getAgencyId()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .agency(agency)
                .build();

        User saved = userRepository.save(user);
        auditLogService.log("CREATE_USER", "User", saved.getId(),
                "Creation utilisateur " + saved.getEmail()
                        + " dans agence " + agency.getCode());
        return userMapper.toResponse(saved);
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable: " + id));

        assertSameAgencyIfDirector(user);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getAgencyId() != null) {
            if (SecurityUtils.isDirecteur()) {
                SecurityUser current = SecurityUtils.currentUser();
                if (current.getAgencyId() == null
                        || !current.getAgencyId().equals(request.getAgencyId())) {
                    throw new ForbiddenException(
                            "Vous ne pouvez pas changer l'agence d'un utilisateur");
                }
            }
            Agency agency = agencyRepository.findById(request.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Agence introuvable: " + request.getAgencyId()));
            user.setAgency(agency);
        }

        User saved = userRepository.save(user);
        auditLogService.log("UPDATE_USER", "User", saved.getId(),
                "Mise a jour utilisateur " + saved.getEmail());
        return userMapper.toResponse(saved);
    }

    // ============================================================
    // FIND BY ID
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable: " + id));
        assertSameAgencyIfDirector(user);
        return userMapper.toResponse(user);
    }

    // ============================================================
    // FIND ALL
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    // ============================================================
    // FIND ALL pour le directeur courant
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAllForCurrentDirector(Pageable pageable) {
        SecurityUser current = SecurityUtils.currentUser();
        Long agencyId = current.getAgencyId();
        if (agencyId == null) {
            return Page.empty(pageable);
        }
        return userRepository.findByAgencyId(agencyId, pageable)
                .map(userMapper::toResponse);
    }

    // ============================================================
    // FIND CURRENT USER
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public UserResponse findCurrentUser() {
        User user = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur courant introuvable"));
        return userMapper.toResponse(user);
    }

    // ============================================================
    // RESET PASSWORD (avec choix auto ou manuel)
    // ============================================================
    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(Long id, ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable: " + id));

        assertSameAgencyIfDirector(user);

        // Determine le mot de passe a utiliser
        final String plainPassword;
        final boolean autoGenerated;

        if (request == null
                || request.getNewPassword() == null
                || request.getNewPassword().isBlank()) {
            // Aucun mot de passe fourni => generation automatique
            plainPassword = generateTemporaryPassword();
            autoGenerated = true;
        } else {
            // Mot de passe saisi par le directeur => validation minimale
            String provided = request.getNewPassword().trim();
            if (provided.length() < 8) {
                throw new InvalidStateException(
                        "Le mot de passe doit contenir au moins 8 caracteres");
            }
            if (provided.length() > 100) {
                throw new InvalidStateException(
                        "Le mot de passe ne peut pas depasser 100 caracteres");
            }
            plainPassword = provided;
            autoGenerated = false;
        }

        // Hash + enregistre dans la colonne `password` existante
        user.setPassword(passwordEncoder.encode(plainPassword));
        userRepository.save(user);

        auditLogService.log(
                "RESET_PASSWORD",
                "User",
                user.getId(),
                "Reinitialisation du mot de passe de " + user.getEmail()
                        + " (" + (autoGenerated ? "auto" : "manuel") + ")"
        );

        return ResetPasswordResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .temporaryPassword(plainPassword)
                .autoGenerated(autoGenerated)
                .build();
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private void assertSameAgencyIfDirector(User target) {
        if (!SecurityUtils.isDirecteur()) return;

        SecurityUser current = SecurityUtils.currentUser();
        Long currentAgencyId = current.getAgencyId();
        Long targetAgencyId = target.getAgency() != null
                ? target.getAgency().getId()
                : null;

        if (currentAgencyId == null || targetAgencyId == null
                || !currentAgencyId.equals(targetAgencyId)) {
            throw new ForbiddenException("Acces inter-agence interdit");
        }
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(PWD_LENGTH);
        for (int i = 0; i < PWD_LENGTH; i++) {
            sb.append(PWD_CHARS.charAt(RANDOM.nextInt(PWD_CHARS.length())));
        }
        return sb.toString();
    }
}
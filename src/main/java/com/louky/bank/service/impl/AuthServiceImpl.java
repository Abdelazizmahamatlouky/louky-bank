package com.louky.bank.service.impl;

import com.louky.bank.domain.entity.RefreshToken;
import com.louky.bank.domain.entity.User;
import com.louky.bank.dto.request.LoginRequest;
import com.louky.bank.dto.response.AuthResponse;
import com.louky.bank.exception.UnauthorizedException;
import com.louky.bank.mapper.UserMapper;
import com.louky.bank.repository.RefreshTokenRepository;
import com.louky.bank.repository.UserRepository;
import com.louky.bank.security.JwtService;
import com.louky.bank.security.SecurityUser;
import com.louky.bank.service.AuditLogService;
import com.louky.bank.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    // ============================================================
    // LOGIN
    // ============================================================
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour : {}", request.getEmail());

        // ✅ 1. VERIFICATION PREALABLE : le compte est-il actif ?
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent() && !existingUser.get().isActive()) {
            User user = existingUser.get();
            auditLogService.log(
                    "LOGIN_DISABLED",
                    "User",
                    user.getId(),
                    "Tentative de connexion sur compte inactif: " + request.getEmail()
            );
            log.warn("Login refuse : compte desactive ({})", request.getEmail());

            throw new UnauthorizedException(
                    "Votre compte est desactive. Contactez votre directeur d'agence "
                            + "pour le reactiver."
            );
        }

        // ✅ 2. AUTHENTIFICATION NORMALE
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            log.info("Connexion reussie pour : {}", request.getEmail());
            return issueTokens(securityUser);
        } catch (DisabledException ex) {
            auditLogService.log(
                    "LOGIN_DISABLED",
                    "User",
                    existingUser.map(User::getId).orElse(null),
                    "Tentative de connexion sur compte inactif: " + request.getEmail()
            );
            log.warn("Login refuse : compte desactive via DisabledException ({})",
                    request.getEmail());
            throw new UnauthorizedException(
                    "Votre compte est desactive. Contactez votre directeur d'agence "
                            + "pour le reactiver."
            );
        } catch (LockedException ex) {
            log.warn("Login refuse : compte verrouille ({})", request.getEmail());
            throw new UnauthorizedException(
                    "Votre compte est verrouille. Contactez votre administrateur."
            );
        } catch (AuthenticationException ex) {
            log.warn("Login refuse : identifiants invalides ({})", request.getEmail());
            throw new UnauthorizedException("Email ou mot de passe incorrect.");
        }
    }

    // ============================================================
    // REFRESH
    // ============================================================
    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalide"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expire ou revoque");
        }
        if (!jwtService.isValid(refreshToken)) {
            throw new UnauthorizedException("Refresh token invalide");
        }

        User user = stored.getUser();
        if (!user.isActive()) {
            throw new UnauthorizedException(
                    "Votre compte est desactive. Veuillez contacter votre directeur d'agence."
            );
        }

        SecurityUser securityUser = new SecurityUser(user);
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(securityUser);
    }

    // ============================================================
    // LOGOUT
    // ============================================================
    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            User user = token.getUser();
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            auditLogService.logAsUser(
                    user.getId(),
                    user.getEmail(),
                    "LOGOUT",
                    "RefreshToken",
                    null,
                    "Deconnexion utilisateur " + user.getEmail()
            );
        });
    }

    // ============================================================
    // ISSUE TOKENS
    // ============================================================
    private AuthResponse issueTokens(SecurityUser securityUser) {
        String accessToken = jwtService.generateAccessToken(securityUser);
        String refreshToken = jwtService.generateRefreshToken(securityUser);
        User user = userRepository.findById(securityUser.getId())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));

        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpirationMs()))
                .build();
        refreshTokenRepository.save(entity);

        auditLogService.logAsUser(
                user.getId(),
                user.getEmail(),
                "LOGIN",
                "User",
                user.getId(),
                "Connexion reussie: " + user.getEmail()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .user(userMapper.toResponse(user))
                .build();
    }
}
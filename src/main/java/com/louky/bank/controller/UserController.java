package com.louky.bank.controller;

import com.louky.bank.dto.request.ResetPasswordRequest;
import com.louky.bank.dto.request.UserCreateRequest;
import com.louky.bank.dto.request.UserUpdateRequest;
import com.louky.bank.dto.response.PageResponse;
import com.louky.bank.dto.response.ResetPasswordResponse;
import com.louky.bank.dto.response.UserResponse;
import com.louky.bank.service.UserService;
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

@Tag(name = "Utilisateurs")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(
                userService.findAllForCurrentDirector(pageable)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.findCurrentUser());
    }

    /**
     * Reinitialise le mot de passe d'un utilisateur de l'agence du directeur.
     *
     * Le body est optionnel :
     *  - Si `newPassword` est absent ou vide => generation automatique.
     *  - Sinon => utilise le mot de passe fourni (doit faire >= 8 caracteres).
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @PathVariable Long id,
            @RequestBody(required = false) ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(id, request));
    }
}
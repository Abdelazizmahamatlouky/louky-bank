package com.louky.bank.service;

import com.louky.bank.dto.request.ResetPasswordRequest;
import com.louky.bank.dto.request.UserCreateRequest;
import com.louky.bank.dto.request.UserUpdateRequest;
import com.louky.bank.dto.response.ResetPasswordResponse;
import com.louky.bank.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    UserResponse findById(Long id);

    Page<UserResponse> findAll(Pageable pageable);

    UserResponse findCurrentUser();

    /**
     * Liste des utilisateurs de l'agence du directeur courant.
     */
    Page<UserResponse> findAllForCurrentDirector(Pageable pageable);

    /**
     * Reinitialise le mot de passe d'un utilisateur de l'agence du directeur.
     *
     * Si `request.newPassword` est null ou vide, un mot de passe aleatoire
     * est genere. Sinon, le mot de passe fourni est utilise.
     *
     * Dans les deux cas, le mot de passe est immediatement hashé avec BCrypt
     * et n'est jamais stocke en clair en base.
     */
    ResetPasswordResponse resetPassword(Long id, ResetPasswordRequest request);
}
package com.louky.bank.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Requete de reinitialisation de mot de passe.
 *
 * Si `newPassword` est null ou vide, un mot de passe aleatoire sera genere.
 * Sinon, le mot de passe fourni sera utilise (et doit respecter les regles
 * de complexite minimales).
 */
@Getter
@Setter
public class ResetPasswordRequest {

    /**
     * Mot de passe choisi manuellement par le directeur.
     * Null ou vide => generation automatique.
     */
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caracteres")
    private String newPassword;
}
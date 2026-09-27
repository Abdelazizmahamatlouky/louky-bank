package com.louky.bank.service;

import com.louky.bank.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    /**
     * Journalise une action en utilisant l'utilisateur courant du SecurityContext.
     * A utiliser quand on est dans une requete HTTP authentifiee.
     */
    void log(String action, String entityType, Long entityId, String details);

    /**
     * Journalise une action en forcant l'utilisateur.
     * A utiliser dans les cas ou le SecurityContext n'est pas disponible
     * (listeners, jobs planifies, appels internes, refresh token).
     */
    void logAsUser(Long userId, String userEmail,
                   String action, String entityType, Long entityId, String details);

    Page<AuditLogResponse> findAll(Pageable pageable);

    Page<AuditLogResponse> findByUser(Long userId, Pageable pageable);
}
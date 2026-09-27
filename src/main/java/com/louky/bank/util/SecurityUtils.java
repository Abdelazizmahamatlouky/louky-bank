package com.louky.bank.util;

import com.louky.bank.exception.UnauthorizedException;
import com.louky.bank.security.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static SecurityUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new UnauthorizedException("Utilisateur non authentifie");
        }
        return securityUser;
    }

    public static Long currentUserId() {
        return currentUser().getId();
    }

    public static boolean isDirecteur() {
        return "DIRECTEUR".equals(currentUser().getRole());
    }

    public static boolean isAgent() {
        return "AGENT".equals(currentUser().getRole());
    }
}
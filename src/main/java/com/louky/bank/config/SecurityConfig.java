package com.louky.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.louky.bank.dto.response.ApiErrorResponse;
import com.louky.bank.security.CustomUserDetailsService;
import com.louky.bank.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Retourne 401 quand le token est manquant, expire ou invalide.
     *
     * Adapte le message selon le type d'exception pour ne pas masquer les cas
     * specifiques (compte desactive, verrouille, identifiants invalides).
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            final String message;

            if (authException instanceof DisabledException) {
                message = "Votre compte est desactive. Contactez votre directeur "
                        + "d'agence pour le reactiver.";
            } else if (authException instanceof LockedException) {
                message = "Votre compte est verrouille. Contactez votre administrateur.";
            } else if (authException instanceof BadCredentialsException) {
                message = "Email ou mot de passe incorrect.";
            } else if (authException != null
                    && authException.getMessage() != null
                    && !authException.getMessage().isBlank()) {
                message = authException.getMessage();
            } else {
                message = "Session expiree. Veuillez vous reconnecter.";
            }

            writeError(response, request, HttpStatus.UNAUTHORIZED, message);
        };
    }

    /**
     * Retourne 403 uniquement quand l'utilisateur est authentifie mais
     * n'a pas les droits necessaires.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> writeError(
                response,
                request,
                HttpStatus.FORBIDDEN,
                "Acces refuse a cette ressource."
        );
    }

    /**
     * Ecrit un body JSON d'erreur dans la reponse HTTP.
     *
     * ✅ CORRECTION CRITIQUE : on verifie que la response n'est pas
     * deja committée avant d'ecrire, sinon on obtient un 500.
     */
    private void writeError(HttpServletResponse response,
                            HttpServletRequest request,
                            HttpStatus status,
                            String message) throws IOException {

        // ✅ Ne pas ecrire si la reponse est deja committée par le controller
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/agencies/**").hasAnyRole("AGENT", "DIRECTEUR")
                        .requestMatchers("/api/v1/agencies/**").hasRole("DIRECTEUR")
                        .requestMatchers("/api/v1/users/**").hasRole("DIRECTEUR")
                        .requestMatchers("/api/v1/audit-logs/**").hasRole("DIRECTEUR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/credit-requests/*/submit").hasRole("AGENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/credit-requests/*/decision").hasRole("DIRECTEUR")
                        .requestMatchers("/api/v1/credit-requests/**").hasAnyRole("AGENT", "DIRECTEUR")
                        .requestMatchers("/api/v1/credit-assessments/**").hasAnyRole("AGENT", "DIRECTEUR")
                        .requestMatchers("/api/v1/credit-decisions/**").hasAnyRole("AGENT", "DIRECTEUR")
                        .requestMatchers("/api/v1/notifications/**").hasAnyRole("AGENT", "DIRECTEUR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
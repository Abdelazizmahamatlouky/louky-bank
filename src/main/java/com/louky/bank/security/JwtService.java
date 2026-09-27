package com.louky.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(SecurityUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(properties.getIssuer())
                .claims(Map.of(
                        "uid", user.getId(),
                        "role", user.getRole(),
                        "agencyId", user.getAgencyId() == null ? 0L : user.getAgencyId()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(properties.getAccessTokenExpirationMs())))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(SecurityUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(properties.getIssuer())
                .claim("uid", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(properties.getRefreshTokenExpirationMs())))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public long getAccessTokenExpirationMs() {
        return properties.getAccessTokenExpirationMs();
    }

    public long getRefreshTokenExpirationMs() {
        return properties.getRefreshTokenExpirationMs();
    }
}
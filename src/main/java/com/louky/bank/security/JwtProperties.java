package com.louky.bank.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "loukybank.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMs = 900_000L;
    private long refreshTokenExpirationMs = 604_800_000L;
    private String issuer = "louky-bank-creditflow";
}
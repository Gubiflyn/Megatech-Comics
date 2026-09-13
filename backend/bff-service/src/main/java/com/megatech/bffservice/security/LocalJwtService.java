package com.megatech.bffservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Emite y respalda la validación de JWT locales para clientes autenticados
 * con email + contraseña propia (no Azure AD). Firma con HS256 usando una
 * clave compartida configurada en app.security.local-jwt.secret.
 */
@Service
public class LocalJwtService {

    private final SecretKey secretKey;
    private final String issuer;
    private final long expirationMinutes;

    public LocalJwtService(
            @Value("${app.security.local-jwt.secret}") String secret,
            @Value("${app.security.local-jwt.issuer}") String issuer,
            @Value("${app.security.local-jwt.expiration-minutes}") long expirationMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public String generarToken(String clienteUuid, String email) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(clienteUuid)
                .claim("email", email)
                .claim("roles", List.of("CLIENTE"))
                .issuer(issuer)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public String getIssuer() {
        return issuer;
    }
}

package com.williammedina.forohub.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.williammedina.forohub.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String ISSUER = "ForoHub";
    public static final long ACCESS_TOKEN_EXPIRATION = ( 15 ) * 60; // 15 minutos en segundos
    public static final long REFRESH_TOKEN_EXPIRATION = ( 30 ) * 24 * 60 * 60; // 30 días en segundos

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION * 1000);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION * 1000);
    }

    public String generateToken(User user, long expirationMillis) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(user.getId()))
                    .withClaim("username", user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                    .sign(getAlgorithm());
        } catch (JWTCreationException e) {
            log.error("Error al generar el token para usuario {}: {}", user.getUsername(), e.getMessage());
            throw new RuntimeException("Error al generar el token", e);
        }
    }

    public String getSubjectFromToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("Intento de verificar un token nulo o vacío.");
            throw new IllegalArgumentException("El token no puede ser nulo");
        }

        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                .withIssuer(ISSUER)
                .build()
                .verify(token);
        String subject = decodedJWT.getSubject();
        if (subject == null || subject.isBlank()) {
            log.warn("Token inválido: no se encontró el campo 'subject' después de la verificación.");
            throw new JWTVerificationException("El campo 'sujeto' no está presente en el token");
        }
        return subject;
    }
}

package com.williammedina.forohub.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.williammedina.forohub.domain.user.entity.UserEntity;
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
    public static final long ACCESS_TOKEN_EXPIRATION = ( 15 ) * 60; // 15 minutes in seconds
    public static final long REFRESH_TOKEN_EXPIRATION = ( 30 ) * 24 * 60 * 60; // 30 days in seconds

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken(UserEntity user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION * 1000);
    }

    public String generateRefreshToken(UserEntity user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION * 1000);
    }

    public String generateToken(UserEntity user, long expirationMillis) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(user.getId()))
                    .withClaim("username", user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                    .sign(getAlgorithm());
        } catch (JWTCreationException e) {
            log.error("Error generating token for user {}: {}", user.getUsername(), e.getMessage());
            throw new RuntimeException("Error al generar el token", e);
        }
    }

    public String getSubjectFromToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("Attempt to verify null or empty token.");
            throw new IllegalArgumentException("El token no puede ser nulo");
        }

        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                .withIssuer(ISSUER)
                .build()
                .verify(token);
        String subject = decodedJWT.getSubject();
        if (subject == null || subject.isBlank()) {
            log.warn("Invalid token: 'subject' field not found after verification.");
            throw new JWTVerificationException("El campo 'sujeto' no está presente en el token");
        }
        return subject;
    }
}

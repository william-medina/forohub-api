package com.williammedina.forohub.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.williammedina.forohub.domain.user.User;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final String jwtSecret = Dotenv.load().get("JWT_SECRET");

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.create()
                    .withIssuer("ForoHub")
                    .withSubject(user.getUsername())
                    .withClaim("id", user.getId())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error al generar el token", exception);
        }
    }

    public String getSubjectFromToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("El token no puede ser nulo");
        }

        DecodedJWT decodedJWT = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            decodedJWT = JWT.require(algorithm)
                    .withIssuer("ForoHub")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            //throw new RuntimeException("Token inválido: " + exception.getMessage(), exception);
            throw exception;
        }

        if (decodedJWT == null || decodedJWT.getSubject() == null) {
            throw new RuntimeException("Token inválido: El campo 'sujeto' no está presente");
        }
        return decodedJWT.getSubject();
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-05:00"));
    }
}

package com.williammedina.forohub.infrastructure.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del servidor con el token JWT")
public record JwtTokenResponse(
        @Schema(description = "Token JWT generado (access token)", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String accessToken
) {
}

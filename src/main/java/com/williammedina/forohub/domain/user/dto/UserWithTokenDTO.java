package com.williammedina.forohub.domain.user.dto;

public record UserWithTokenDTO(
        Long id,
        String username,
        String profile,
        String token
) {
}

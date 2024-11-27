package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
        @NotBlank(message = "El nombre de usuario es requerido.")
        String username,

        @NotBlank(message = "El password es requerido.")
        String password
) {
}

package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para iniciar sesión")
public record LoginUserDTO(
        @Schema(description = "Nombre de usuario", example = "usuario123")
        @NotBlank(message = "El nombre de usuario es requerido.")
        String username,

        @Schema(description = "Contraseña", example = "miPassword123")
        @NotBlank(message = "El password es requerido.")
        String password
) {
}

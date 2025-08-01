package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para recibir el email del usuario")
public record EmailUserDTO(
        @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.com")
        @NotBlank(message = "El email es requerido.")
        @Email(message = "Email no válido.")
        String email
) {
}

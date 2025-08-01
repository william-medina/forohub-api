package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar la contraseña mediante token de recuperación")
public record UpdatePasswordWithTokenDTO(
        @Schema(description = "Nueva contraseña", example = "nuevoPassword123")
        @NotBlank(message = "El password es requerido.")
        @Size(min = 8, message = "El password debe tener al menos 8 caracteres.")
        String password,

        @Schema(description = "Confirmación de la contraseña", example = "nuevoPassword123")
        @NotBlank(message = "Es necesario confirmar el password.")
        String password_confirmation
) {
}

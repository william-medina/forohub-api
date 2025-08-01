package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para cambiar la contraseña actual del usuario")
public record UpdateCurrentUserPasswordDTO(

        @Schema(description = "Contraseña actual", example = "password123")
        @NotBlank(message = "El password actual es requerido.")
        String current_password,

        @Schema(description = "Nueva contraseña", example = "nuevoPassword123")
        @NotBlank(message = "El nuevo password es requerido.")
        @Size(min = 8, message = "El nuevo password debe tener al menos 8 caracteres.")
        String password,

        @Schema(description = "Confirmación del nuevo password", example = "nuevoPassword123")
        @NotBlank(message = "Es necesario confirmar el nuevo password.")
        String password_confirmation
) {
}

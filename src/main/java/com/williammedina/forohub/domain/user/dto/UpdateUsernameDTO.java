package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar el nombre de usuario")
public record UpdateUsernameDTO(
        @Schema(description = "Nuevo nombre de usuario", example = "nuevoUsuario")
        @NotBlank(message = "El nombre de usuario es requerido.")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe contener entre 3 y 20 caracteres.")
        String username
) {
}

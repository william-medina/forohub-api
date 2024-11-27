package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdatePasswordWithTokenDTO(
        @NotBlank(message = "El password es requerido.")
        @Size(min = 8, message = "El password debe tener al menos 8 caracteres.")
        String password,

        @NotBlank(message = "Es necesario confirmar el password.")
        String password_confirmation
) {
}

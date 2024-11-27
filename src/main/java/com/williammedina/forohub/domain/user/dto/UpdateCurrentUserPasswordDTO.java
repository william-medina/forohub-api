package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCurrentUserPasswordDTO(

        @NotBlank(message = "El password actual es requerido.")
        String current_password,

        @NotBlank(message = "El nuevo password es requerido.")
        @Size(min = 8, message = "El nuevo password debe tener al menos 8 caracteres.")
        String password,

        @NotBlank(message = "Es necesario confirmar el nuevo password.")
        String password_confirmation
) {
}

package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailUserDTO(
        @NotBlank(message = "El email es requerido.")
        @Email(message = "Email no válido.")
        String email
) {
}

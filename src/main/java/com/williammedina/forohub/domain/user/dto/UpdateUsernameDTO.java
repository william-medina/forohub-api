package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUsernameDTO(
        @NotBlank(message = "El nombre de usuario es requerido.")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe contener entre 3 y 20 caracteres.")
        String username
) {
}

package com.williammedina.forohub.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(

        @NotBlank(message = "El email es requerido.")
        @Size(max = 100, message = "El email no debe exceder los 100 caracteres.")
        @Email(message = "Email no válido.")
        String email,

        @NotBlank(message = "El nombre de usuario es requerido.")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe contener entre 3 y 20 caracteres.")
        String username,

        @NotBlank(message = "El password es requerido.")
        @Size(min = 8, message = "El password debe tener al menos 8 caracteres.")
        String password,

        @NotBlank(message = "Es necesario confirmar el password.")
        String password_confirmation
) {

}

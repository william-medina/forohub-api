package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para crear un nuevo usuario")
public record CreateUserDTO(

        @Schema(description = "Correo electrónico del usuario", example = "correo@ejemplo.com")
        @NotBlank(message = "El email es requerido.")
        @Size(max = 100, message = "El email no debe exceder los 100 caracteres.")
        @Email(message = "Email no válido.")
        String email,

        @Schema(description = "Nombre de usuario", example = "usuario123")
        @NotBlank(message = "El nombre de usuario es requerido.")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe contener entre 3 y 20 caracteres.")
        String username,

        @Schema(description = "Contraseña", example = "miPassword123")
        @NotBlank(message = "El password es requerido.")
        @Size(min = 8, message = "El password debe tener al menos 8 caracteres.")
        String password,

        @Schema(description = "Confirmación de la contraseña", example = "miPassword123")
        @NotBlank(message = "Es necesario confirmar el password.")
        String password_confirmation
) {

}

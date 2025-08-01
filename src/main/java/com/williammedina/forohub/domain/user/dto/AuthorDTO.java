package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Autor del contenido")
public record AuthorDTO(
        @Schema(description = "Nombre de usuario del autor", example = "William Medina")
        String username,

        @Schema(description = "Rol del autor", example = "INSTRUCTOR")
        String profile
) {
}

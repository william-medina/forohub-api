package com.williammedina.forohub.domain.reply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para actualizar el contenido de una respuesta.")
public record UpdateReplyDTO(

        @Schema(description = "Nuevo contenido de la respuesta", example = "He encontrado la solución, era un problema en la configuración del bean.")
        @NotBlank(message = "El contenido de la respuesta es obligatorio.")
        String content
) {
}

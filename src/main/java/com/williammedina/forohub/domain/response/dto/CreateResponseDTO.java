package com.williammedina.forohub.domain.response.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos necesarios para crear una nueva respuesta en un tópico.")
public record CreateResponseDTO(

        @Schema(description = "ID del tópico al que pertenece la respuesta", example = "12")
        @NotNull(message = "El ID del post es obligatorio.")
        Long topicId,

        @Schema(description = "Contenido de la respuesta", example = "Puedes habilitar los endpoints asegurándote de usar @RestController y de tener bien configurado el archivo application.properties.")
        @NotBlank(message = "El contenido de la respuesta es obligatorio.")
        String content
) {
}

package com.williammedina.forohub.domain.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear un nuevo tópico")
public record InputTopicDTO(

        @Schema(description = "Título del tópico", example = "Error al ejecutar aplicación Spring Boot")
        @NotBlank(message = "El título es requerido.")
        @Size(max = 100, message = "El título debe tener como máximo 100 caracteres.")
        String title,

        @Schema(description = "Descripción del tópico", example = "Estoy intentando ejecutar mi aplicación, pero falla al iniciar sin mostrar un mensaje claro.")
        @NotBlank(message = "La descripción es requerida.")
        String description,

        @Schema(description = "ID del curso al que pertenece el tópico", example = "2")
        @NotNull(message = "El curso es requerido.")
        Long courseId
) {
}

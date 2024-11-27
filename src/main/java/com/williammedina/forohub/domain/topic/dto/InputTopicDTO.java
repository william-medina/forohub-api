package com.williammedina.forohub.domain.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InputTopicDTO(

        @NotBlank(message = "El título es requerido.")
        @Size(max = 100, message = "El título debe tener como máximo 100 caracteres.")
        String title,

        @NotBlank(message = "La descripción es requerida.")
        String description,

        @NotNull(message = "El curso es requerido.")
        Long courseId
) {
}

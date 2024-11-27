package com.williammedina.forohub.domain.response.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateResponseDTO(
        @NotNull(message = "El ID del post es obligatorio.")
        Long topicId,

        @NotBlank(message = "El contenido de la respuesta es obligatorio.")
        String content
) {
}

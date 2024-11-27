package com.williammedina.forohub.domain.response.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateResponseDTO(
        @NotBlank(message = "El contenido de la respuesta es obligatorio.")
        String content
) {
}

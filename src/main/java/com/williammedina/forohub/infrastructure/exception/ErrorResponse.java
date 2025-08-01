package com.williammedina.forohub.infrastructure.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Respuesta de error general")
public class ErrorResponse {
    @Schema(description = "Mensaje descriptivo del error", example = "Tópico no encontrado")
    private String error;
}

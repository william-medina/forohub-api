package com.williammedina.forohub.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta estándar de error de la API")
public class ApiErrorResponse {

    @Schema(description = "Fecha y hora en la que ocurrió el error", example = "2025-01-01T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP asociado al error", example = "400")
    private int status;

    @Schema(description = "Nombre del estado HTTP asociado al error", example = "BAD_REQUEST")
    private String error;

    @Schema(description = "Mensaje principal descriptivo del error", example = "Error en la solicitud.")
    private String message;

    @Schema(
            description = "Mapa opcional de errores por campo. Usado principalmente en validaciones de formularios.",
            example = "{\"field1\": \"Debe ser obligatorio\", \"field2\": \"Formato inválido\"}"
    )
    private Map<String, String> errors;

    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/api/endpoint")
    private String path;
}

package com.williammedina.forohub.infrastructure.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de respuesta genérico utilizado por el servidor para devolver un mensaje informativo o de confirmación de una operación.")
public record MessageResponse(

        @Schema(
                description = "Contenido del mensaje devuelto por el servidor. Usualmente describe el resultado de la operación solicitada.",
                example = "La sesión se cerró correctamente."
        )
        String message
) {}
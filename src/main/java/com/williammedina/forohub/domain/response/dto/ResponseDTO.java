package com.williammedina.forohub.domain.response.dto;

import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta visible dentro del detalle de un tópico.")
public record ResponseDTO(

        @Schema(description = "ID único de la respuesta", example = "25")
        Long id,

        @Schema(description = "ID del tópico asociado a la respuesta", example = "5")
        Long topicId,

        @Schema(description = "Contenido de la respuesta", example = "Puedes habilitar los endpoints asegurándote de usar @RestController y de tener bien configurado el archivo application.properties..")
        String content,

        @Schema(description = "Datos del autor de la respuesta", example = "Alejandro Cristiano")
        UserDTO author,

        @Schema(description = "Indica si esta respuesta fue marcada como solución", example = "true")
        Boolean solution,

        @Schema(description = "Fecha en que se creó la respuesta", example = "2025-05-31T16:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de la última edición", example = "2025-07-01T08:30:00")
        LocalDateTime updatedAt
) {
        public static ResponseDTO fromEntity(Response response) {

                UserDTO author = new UserDTO(
                        response.getUser().getId(),
                        response.getUser().getUsername(),
                        response.getUser().getProfile().getName()
                );

                return new ResponseDTO(
                        response.getId(),
                        response.getTopic().getId(),
                        response.getContent(),
                        author,
                        response.getSolution(),
                        response.getCreatedAt(),
                        response.getUpdatedAt()
                );
        }
}

package com.williammedina.forohub.domain.topic.dto;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos generales del tópico")
public record TopicDTO(

        @Schema(description = "ID del tópico", example = "12")
        Long id,

        @Schema(description = "Título del tópico", example = "Error al ejecutar aplicación Spring Boot")
        String title,

        @Schema(description = "Descripción del tópico", example = "Estoy intentando ejecutar mi aplicación, pero falla al iniciar sin mostrar un mensaje claro.")
        String description,

        @Schema(description = "Nombre del curso al que pertenece el tópico", example = "Desarrollo de Aplicaciones Web con Spring Boot")
        String course,

        @Schema(description = "Nombre de la categoría del curso", example = "Spring Boot")
        String category,

        @Schema(description = "Nombre del autor del tópico", example = "William Medina")
        String author,

        @Schema(description = "Cantidad de respuestas asociadas al tópico", example = "3")
        Integer repliesCount,

        @Schema(description = "Estado actual del tópico", example = "CLOSED")
        TopicEntity.Status status,

        @Schema(description = "Fecha de creación del tópico", example = "2025-05-31T15:45:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización del tópico", example = "2025-07-01T10:15:00")
        LocalDateTime updatedAt
) {
        public static TopicDTO fromEntity(TopicEntity topic) {
                return new TopicDTO(
                        topic.getId(),
                        topic.getTitle(),
                        topic.getDescription(),
                        topic.getCourse().getName(),
                        topic.getCourse().getCategory(),
                        topic.getUser().getUsername(),
                        topic.getReplies().size(),
                        topic.getStatus(),
                        topic.getCreatedAt(),
                        topic.getUpdatedAt()
                );
        }

}

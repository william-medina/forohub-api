package com.williammedina.forohub.domain.notification.dto;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos generales de una notificación")
public record NotificationDTO(

        @Schema(description = "ID de la notificación", example = "18")
        Long id,

        @Schema(description = "Nombre de usuario que recibe la notificación", example = "William Medina")
        String username,

        @Schema(description = "ID del tópico relacionado", example = "12")
        Long topicId,

        @Schema(description = "Tipo de notificación", example = "TOPIC")
        NotificationEntity.Type type,

        @Schema(description = "Subtipo de notificación", example = "REPLY")
        NotificationEntity.Subtype subtype,

        @Schema(description = "Título de la notificación", example = "Nueva respuesta a tu tópico")
        String title,

        @Schema(description = "Mensaje de la notificación", example = "Tu tópico ha recibido una nueva respuesta. Alejandro Cristiano respondió al tópico 'Error en instalación de JDK' del curso: Introducción a Java y Programación Orientada a Objetos")
        String message,

        @Schema(description = "Estado de lectura de la notificación", example = "false")
        Boolean isRead,

        @Schema(description = "Fecha de creación de la notificación", example = "2025-07-31T14:45:00")
        LocalDateTime createdAt
) {

        public static NotificationDTO fromEntity(NotificationEntity notification) {

                // Long topicId = (notification.getTopic() != null) ? notification.getTopic().getId() : null;
                Long topicId = (notification.getTopic() == null || notification.getTopic().getIsDeleted()) ? null : notification.getTopic().getId();

                return new NotificationDTO(
                        notification.getId(),
                        notification.getUser().getUsername(),
                        topicId,
                        notification.getType(),
                        notification.getSubtype(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getCreatedAt()
                );
        }
}

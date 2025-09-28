package com.williammedina.forohub.domain.topic.dto;

import com.williammedina.forohub.domain.course.dto.CourseDTO;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Detalle completo del tópico, incluyendo autor, respuestas y seguidores.")
public record TopicDetailsDTO(

        @Schema(description = "ID del tópico", example = "12")
        Long id,

        @Schema(description = "Título del tópico", example = "Error al ejecutar aplicación Spring Boot")
        String title,

        @Schema(description = "Descripción del tópico", example = "Estoy intentando ejecutar mi aplicación, pero falla al iniciar sin mostrar un mensaje claro.")
        String description,

        @Schema(description = "Nombre del curso al que pertenece el tópico", example = "Desarrollo de Aplicaciones Web con Spring Boot")
        CourseDTO course,

        @Schema(description = "Datos del autor del tópico", example = "William Medina")
        UserDTO author,

        @Schema(description = "Lista de respuestas asociadas al tópico")
        List<ReplyDTO> replies,

        @Schema(description = "Estado actual del tópico", example = "ACTIVE")
        TopicEntity.Status status,

        @Schema(description = "Fecha de creación del tópico", example = "2025-05-31T15:45:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización del tópico", example = "2025-07-01T10:15:00")
        LocalDateTime updateAt,

        @Schema(description = "Lista de seguidores del tópico")
        List<TopicFollowerDTO> followers
) {
        public static TopicDetailsDTO fromEntity(TopicEntity topic, List<ReplyDTO> replies) {

                UserDTO author = new UserDTO(
                        topic.getUser().getId(),
                        topic.getUser().getUsername(),
                        topic.getUser().getProfile().getName()
                );

                CourseDTO course = new CourseDTO(
                        topic.getCourse().getId(),
                        topic.getCourse().getName(),
                        topic.getCourse().getCategory()
                );

                List<TopicFollowerDTO> followers = topic.getFollowedTopics().stream()
                        .map(topicFollow -> new TopicFollowerDTO(
                                new UserDTO(
                                        topicFollow.getUser().getId(),
                                        topicFollow.getUser().getUsername(),
                                        topicFollow.getUser().getProfile().getName()
                                ),
                                topicFollow.getFollowedAt()
                        ))
                        .toList();


                return new TopicDetailsDTO(
                        topic.getId(),
                        topic.getTitle(),
                        topic.getDescription(),
                        course,
                        author,
                        replies,
                        topic.getStatus(),
                        topic.getCreatedAt(),
                        topic.getUpdatedAt(),
                        followers
                );
        }
}

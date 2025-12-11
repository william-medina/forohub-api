package com.williammedina.forohub.domain.topicfollow.dto;

import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

@Schema(description = "Detalle del seguimiento a un tópico")
public record TopicFollowDetailsDTO(
        @Schema(description = "Información del tópico seguido")
        TopicDTO topic,

        @Schema(description = "Fecha en la que se siguió el tópico", example = "2025-07-31T15:00:00")
        LocalDateTime followedAt
) {

    public static TopicFollowDetailsDTO fromEntity(TopicFollowEntity topicFollow) {

        return new TopicFollowDetailsDTO(
                TopicDTO.fromEntity(topicFollow.getTopic()),
                topicFollow.getFollowedAt()
        );
    }
}

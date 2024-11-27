package com.williammedina.forohub.domain.topicfollow.dto;

import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record TopicFollowDetailsDTO(
        TopicDTO topic,
        @Nullable LocalDateTime followedAt
) {
}

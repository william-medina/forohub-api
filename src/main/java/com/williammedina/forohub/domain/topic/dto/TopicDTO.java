package com.williammedina.forohub.domain.topic.dto;

import com.williammedina.forohub.domain.topic.Topic;

import java.time.LocalDateTime;

public record TopicDTO(
        Long id,
        String title,
        String description,
        String course,
        String category,
        String author,
        Integer responsesCount,
        Topic.Status status,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
}

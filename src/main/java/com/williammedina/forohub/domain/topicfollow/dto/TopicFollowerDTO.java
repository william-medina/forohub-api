package com.williammedina.forohub.domain.topicfollow.dto;

import com.williammedina.forohub.domain.user.dto.UserDTO;

import java.time.LocalDateTime;

public record TopicFollowerDTO(
        UserDTO user,
        LocalDateTime followedAt
) {
}

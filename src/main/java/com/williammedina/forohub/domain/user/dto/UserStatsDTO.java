package com.williammedina.forohub.domain.user.dto;

public record UserStatsDTO(
        Long topicsCount,
        Long responsesCount,
        Long followedTopicsCount
) {
}

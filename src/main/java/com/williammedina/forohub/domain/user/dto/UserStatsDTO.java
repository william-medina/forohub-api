package com.williammedina.forohub.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estadísticas del usuario")
public record UserStatsDTO(
        @Schema(description = "Cantidad de tópicos creados", example = "15")
        Long topicsCount,

        @Schema(description = "Cantidad de respuestas publicadas", example = "43")
        Long responsesCount,

        @Schema(description = "Cantidad de tópicos seguidos", example = "9")
        Long followedTopicsCount
) {
}

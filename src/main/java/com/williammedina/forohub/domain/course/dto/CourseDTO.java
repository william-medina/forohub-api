package com.williammedina.forohub.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos generales del curso")
public record CourseDTO(

        @Schema(description = "ID del curso", example = "5")
        Long id,

        @Schema(description = "Nombre del curso", example = "Spring Boot Avanzado")
        String name,

        @Schema(description = "Categoría del curso", example = "Java")
        String category
) {
}

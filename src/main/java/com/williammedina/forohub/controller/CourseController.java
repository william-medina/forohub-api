package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.course.CourseService;
import com.williammedina.forohub.domain.course.dto.CourseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/course", produces = "application/json")
@Tag(name = "Course", description = "Endpoints para la gestión de cursos disponibles en la app.")
@AllArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Obtener todos los cursos disponibles",
            description = "Devuelve una lista de todos los cursos disponibles en la plataforma.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente."),
            }
    )
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
}

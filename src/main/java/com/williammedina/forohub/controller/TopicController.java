package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.service.TopicService;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.forohub.domain.topicfollow.service.TopicFollowService;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.forohub.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/topic", produces = "application/json")
@Tag(name = "Topic", description = "Endpoints para la gestión de tópicos del foro.")
@AllArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final TopicFollowService topicFollowService;

    @Operation(
            summary = "Crear un nuevo tópico",
            description = "Permite crear un nuevo tópico con los datos proporcionados.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tópico creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "Contenido inapropiado detectado por la IA.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "Título o descripción ya existe en otro tópico.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(@RequestBody @Valid InputTopicDTO topicRequest) {
        TopicDTO topic = topicService.createTopic(topicRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    @Operation(
            summary = "Obtener todos los tópicos",
            description = "Permite obtener una lista de todos los tópicos, con paginación y filtrado opcional por curso, palabra clave y estado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópicos recuperados exitosamente")
            }
    )
    @GetMapping
    public ResponseEntity<Page<TopicDTO>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TopicEntity.Status status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicDTO> topics = topicService.getAllTopics(pageable, courseId, keyword, status);
        return ResponseEntity.ok(topics);
    }

    @Operation(
            summary = "Obtener los tópicos del usuario",
            description = "Permite obtener los tópicos creados por el usuario, con paginación y filtrado opcional por palabra clave.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópicos recuperados exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/user/topics")
    public ResponseEntity<Page<TopicDTO>> getAllTopicsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicDTO> topics = topicService.getAllTopicsByUser(pageable, keyword);
        return ResponseEntity.ok(topics);
    }

    @Operation(
            summary = "Obtener un tópico por ID",
            description = "Permite obtener un tópico específico por su ID, incluyendo todas sus respuestas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópico recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDetailsDTO> getTopicById(@PathVariable Long topicId) {
        TopicDetailsDTO topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(
            summary = "Actualizar un tópico",
            description = "Permite actualizar los detalles de un tópico existente.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópico actualizado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar este tópico o contenido inapropiado detectado por la IA.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "Título o descripción ya existe en otro tópico.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PutMapping("/{topicId}")
    public ResponseEntity<TopicDetailsDTO> updateTopic(@RequestBody @Valid InputTopicDTO topicRequest, @PathVariable Long topicId) throws MessagingException {
        TopicDetailsDTO topic = topicService.updateTopic(topicRequest, topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(
            summary = "Eliminar un tópico",
            description = "Permite eliminar un tópico existente por su ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tópico eliminado exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar este tópico.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) throws MessagingException {
        topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Seguir o dejar de seguir un tópico",
            description = "Permite a un usuario seguir un tópico específico o dejar de seguirlo, gestionando la relación entre el usuario y el tópico en una tabla de asociación.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relación de seguimiento del tópico actualizada exitosamente "),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "No se puede seguir un tópico creado por el mismo usuario.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/follow/{topicId}")
    public ResponseEntity<TopicFollowDetailsDTO> toggleFollowTopic(@PathVariable Long topicId) {
        TopicFollowDetailsDTO topicFollow = topicFollowService.toggleFollowTopic(topicId);
        return ResponseEntity.ok(topicFollow);
    }

    @Operation(
            summary = "Obtener los tópicos seguidos por el usuario",
            description = "Permite obtener los tópicos que el usuario sigue, con paginación y filtrado opcional por palabra clave.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópicos seguidos recuperados exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
            }
    )
    @GetMapping("/user/followed-topics")
    public ResponseEntity<Page<TopicFollowDetailsDTO>> getFollowedTopicsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicFollowDetailsDTO> topics = topicFollowService.getFollowedTopicsByUser(pageable, keyword);
        return ResponseEntity.ok(topics);
    }

}

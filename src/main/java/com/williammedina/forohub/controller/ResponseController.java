package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.response.ResponseService;
import com.williammedina.forohub.domain.response.dto.CreateResponseDTO;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.response.dto.UpdateResponseDTO;
import com.williammedina.forohub.infrastructure.exception.ErrorResponse;
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
@RequestMapping(value = "/response", produces = "application/json")
@Tag(name = "Response", description = "Endpoints para la gestión de respuestas en los tópicos del foro.")
@AllArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @Operation(
            summary = "Crear una nueva respuesta",
            description = "Permite a un usuario crear una respuesta para un tópico específico.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Respuesta creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "No se pueden agregar respuestas a un tópico cerrado o contenido inapropiado detectado por la IA.", content = { @Content( schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO> createResponse(@RequestBody @Valid CreateResponseDTO data) throws MessagingException {
        ResponseDTO response = responseService.createResponse(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener todas las respuestas del usuario autenticado",
            description = "Recupera todas las respuestas creadas por el usuario actualmente autenticado con paginación.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuestas recuperadas exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
            }
    )
    @GetMapping("/user/responses")
    public ResponseEntity<Page<ResponseDTO>> getAllResponsesByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseDTO> responses = responseService.getAllResponsesByUser(pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Obtener una respuesta por ID",
            description = "Recupera una respuesta específica utilizando su identificador único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta recuperada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })
            }
    )
    @GetMapping("/{responseId}")
    public ResponseEntity<ResponseDTO> getResponseById(@PathVariable Long responseId) {
        ResponseDTO response = responseService.getResponseById(responseId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar una respuesta",
            description = "Permite a un usuario actualizar el contenido de una respuesta específica.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta actualizada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta respuesta o contenido inapropiado detectado por la IA.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping("/{responseId}")
    public ResponseEntity<ResponseDTO> updateResponse(@RequestBody @Valid UpdateResponseDTO data, @PathVariable Long responseId) throws MessagingException {
        ResponseDTO response = responseService.updateResponse(data, responseId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Alternar el estado de una respuesta como solución",
            description = "Permite marcar una respuesta como la solución correcta de un tópico o quitarla como solución si ya estaba marcada.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado de solución actualizado exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta respuesta.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })
            }
    )
    @PatchMapping("/{responseId}")
    public ResponseEntity<ResponseDTO> setCorrectResponse(@PathVariable Long responseId) throws MessagingException {
        ResponseDTO response = responseService.setCorrectResponse(responseId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar una respuesta",
            description = "Permite a un usuario eliminar una respuesta específica.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Respuesta eliminada exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar esta respuesta.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })
            }
    )
    @DeleteMapping("/{responseId}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long responseId) throws MessagingException {
        responseService.deleteResponse(responseId);
        return ResponseEntity.noContent().build();
    }

}

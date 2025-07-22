package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.notification.NotificationService;
import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/notify", produces = "application/json")
@SecurityRequirement(name = "cookieAuth")
@Tag(name = "Notify", description = "Endpoints para la gestión de notificaciones de los usuarios.")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Obtener todas las notificaciones del usuario",
            description = "Devuelve una lista de todas las notificaciones del usuario autenticado, ordenadas por fecha de creación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de notificaciones devuelta exitosamente"),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
            }
    )
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsByUser() {
        List<NotificationDTO> notifications = notificationService.getAllNotificationsByUser();
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Eliminar una notificación",
            description = "Elimina una notificación específica por su ID, si pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente."),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar esta notificación.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{notifyId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notifyId) {
        notificationService.deleteNotification(notifyId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Marcar una notificación como leída",
            description = "Marca como leída una notificación específica por su ID, si pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente."),
                    @ApiResponse(responseCode = "401", description = "El usuario no está autenticado.", content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta notificación.",  content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada.",  content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })
            }
    )
    @PatchMapping("/{notifyId}")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable Long notifyId) {
        NotificationDTO notification = notificationService.markNotificationAsRead(notifyId);
        return ResponseEntity.ok(notification);
    }
}

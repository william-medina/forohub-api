package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.user.UserService;
import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.infrastructure.errors.ErrorResponse;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Tag(name = "Auth", description = "Endpoints para la autenticación de usuarios, gestión de cuentas y operaciones relacionadas con los usuarios.")
@AllArgsConstructor
public class UserController {

    //@Autowired
    private final UserService userService;

    @Operation(
            summary = "Crear una cuenta de usuario",
            description = "Permite registrar una nueva cuenta de usuario en el sistema. El usuario debe proporcionar un nombre de usuario, email y password válidos. Si el registro es exitoso, se enviará un token de confirmación al email registrado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cuenta creada"),
                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Username inapropiado detectado por la IA.", content = { @Content( schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "El nombre de usuario o el email ya están registrados.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error al enviar el email de confirmación de cuenta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/create-account")
    public ResponseEntity<UserDTO> createAccount(@RequestBody @Valid CreateUserDTO data) throws MessagingException {
        UserDTO user = userService.createAccount(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(
            summary = "Confirmar cuenta",
            description = "Confirma la cuenta de usuario utilizando un token de confirmación proporcionado en el email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cuenta confirmada"),
                    @ApiResponse(responseCode = "400", description = "Token inválido o expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "410", description = "El token ha expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/confirm-account/{token}")
    public ResponseEntity<UserDTO> confirmAccount(@PathVariable String token) {
        UserDTO user = userService.confirmAccount(token);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario, genera un token de acceso y un token de actualización, y los almacena en cookies HTTP-only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "La cuenta no está confirmada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error al reenviar el email de confirmación de cuenta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody @Valid LoginUserDTO data, HttpServletResponse response) throws MessagingException {
        JwtTokenResponse responseToken = userService.authenticateAndGenerateToken(data, response);
        return ResponseEntity.ok(responseToken);
    }

    @Operation(
            summary = "Solicitar código de confirmación",
            description = "Genera un nuevo código de confirmación y lo envía al email del usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email enviado"),
                    @ApiResponse(responseCode = "400", description = "Demasiadas solicitudes recientes o token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Email no registrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "La cuenta ya está confirmada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/request-code")
    public ResponseEntity<UserDTO> requestConfirmationCode(@RequestBody @Valid EmailUserDTO data) throws MessagingException {
        UserDTO user = userService.requestConfirmationCode(data);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Recuperar password",
            description = "Genera un token de restablecimiento de password y lo envía al email del usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email enviado"),
                    @ApiResponse(responseCode = "400", description = "Demasiadas solicitudes recientes o token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Email no registrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "La cuenta no está confirmada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<UserDTO> forgotPassword(@RequestBody @Valid EmailUserDTO data) throws MessagingException {
        UserDTO user = userService.forgotPassword(data);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Actualizar password con token",
            description = "Permite al usuario restablecer su password utilizando un token de confirmación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password actualizado"),
                    @ApiResponse(responseCode = "400", description = "Token o datos de solicitud inválidos ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "La cuenta no está confirmada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "410", description = "El token ha expirado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/update-password/{token}")
    public ResponseEntity<UserDTO> updatePasswordWithToken(@PathVariable String token, @RequestBody @Valid UpdatePasswordWithTokenDTO data) {
        UserDTO user = userService.updatePasswordWithToken(token, data);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Actualizar password actual del usuario autenticado",
            description = "Permite al usuario autenticado cambiar su password actual.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password actualizada"),
                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Password actual incorrecta o no autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/update-password")
    public ResponseEntity<UserDTO> updateCurrentUserPassword(@RequestBody @Valid UpdateCurrentUserPasswordDTO data) {
        UserDTO user = userService.updateCurrentUserPassword(data);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Actualizar el nombre de usuario del usuario autenticado",
            description = "Permite al usuario autenticado actualizar su nombre de usuario en el sistema.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nombre de usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Username inapropiado detectado por la IA.", content = { @Content( schema = @Schema(implementation = ErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "El nombre de usuario ya están registrados.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/update-username")
    public ResponseEntity<UserDTO> updateUsername(@RequestBody @Valid UpdateUsernameDTO data) {
        UserDTO user = userService.updateUsername(data);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Obtener estadísticas del usuario autenticado",
            description = "Recupera las estadísticas del usuario, que incluyen la cantidad de tópicos creados y seguidos, así como el número de respuestas publicadas por el usuario.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estadísticas del usuario obtenidas correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        UserStatsDTO userStats = userService.getUserStats();
        return ResponseEntity.ok(userStats);
    }

    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Obtiene los detalles del usuario actualmente autenticado.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Detalles del usuario obtenidos correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Actualizar token de acceso",
            description = "Renueva el token de acceso del usuario utilizando el token de actualización almacenado en las cookies.",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token de acceso actualizado correctamente."),
                    @ApiResponse(responseCode = "401", description = "No autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtTokenResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        JwtTokenResponse TokenResponse = userService.refreshAccessToken(request, response);
        return ResponseEntity.ok(TokenResponse);
    }

    @Operation(
            summary = "Cerrar sesión",
            description = "Finaliza la sesión del usuario eliminando las cookies que almacenan el token de acceso y el token de actualización.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente."),
                    @ApiResponse(responseCode = "401", description = "No autorizado - token inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<JwtTokenResponse> logout(HttpServletResponse response) {
        JwtTokenResponse TokenResponse = userService.logout(response);
        return ResponseEntity.ok(TokenResponse);
    }

}

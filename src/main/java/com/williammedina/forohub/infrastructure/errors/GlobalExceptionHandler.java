package com.williammedina.forohub.infrastructure.errors;

import com.williammedina.forohub.domain.response.dto.CreateResponseDTO;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.user.dto.CreateUserDTO;
import com.williammedina.forohub.domain.user.dto.EmailUserDTO;
import com.williammedina.forohub.domain.user.dto.UpdateCurrentUserPasswordDTO;
import com.williammedina.forohub.domain.user.dto.UpdateUsernameDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        HttpStatus status = getHttpStatusFromErrorCode("INVALID_CREDENTIALS");
        ErrorResponse errorResponse = new ErrorResponse("Las credenciales proporcionadas son incorrectas.");
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        HttpStatus status = getHttpStatusFromErrorCode(ex.getErrorCode());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus getHttpStatusFromErrorCode(String errorCode) {
        return switch (errorCode) {
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "ACCOUNT_NOT_CONFIRMED", "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "TOKEN_EXPIRED" -> HttpStatus.GONE;
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INVALID_CREDENTIALS", "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "SERVICE_UNAVAILABLE" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    // Manejo de errores de validación de formulario (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        // List<String> fieldPriorityOrder = List.of("email", "username", "current_password", "password", "password_confirmation", "title", "description", "courseId" ,"content");

        // Obtiene automáticamente y ordena jerárquicamente los campos de los DTOs según prioridad.
        List<String> fieldPriorityOrder = getAllDTOFields(List.of(
                EmailUserDTO.class,
                UpdateUsernameDTO.class,
                UpdateCurrentUserPasswordDTO.class,
                InputTopicDTO.class,
                CreateResponseDTO.class
        ));

        // Ordenar los errores según la prioridad de los campos
        ErrorResponse error = exception.getFieldErrors()
                .stream()
                .sorted(Comparator.comparingInt(fieldError -> {
                    int index = fieldPriorityOrder.indexOf(fieldError.getField());
                    return index == -1 ? Integer.MAX_VALUE : index;
                }))
                .map(fieldError -> new ErrorResponse(fieldError.getDefaultMessage()))
                .findFirst()
                .orElse(null);

        return error != null ? ResponseEntity.badRequest().body(error) : ResponseEntity.badRequest().build();
    }


    // Metodo que obtiene una lista de todos los nombres de campos de los DTOs especificados.
    private List<String> getAllDTOFields(List<Class<?>> dtoClasses) {
        return dtoClasses.stream()
                .flatMap(dto -> Arrays.stream(dto.getDeclaredFields()))
                .map(Field::getName)
                .distinct()
                .collect(Collectors.toList());
    }
}

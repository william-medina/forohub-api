package com.williammedina.forohub.infrastructure.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

        // Obtiene la clase del DTO asociado a la solicitud actual
        Class<?> dtoClass = Optional.ofNullable(exception.getBindingResult().getTarget())
                .map(Object::getClass)
                .orElse(null);

        // Si no se obtiene la clase del DTO, devolver el primer error sin ordenar
        if (dtoClass == null) {
            return exception.getFieldErrors().stream()
                    .findFirst()
                    .map(fieldError -> ResponseEntity.badRequest().body(new ErrorResponse(fieldError.getDefaultMessage())))
                    .orElseGet(() -> ResponseEntity.badRequest().build());
        }

        // Obtiene la lista de nombres de los campos en el orden en que fueron definidos en el DTO
        List<String> fieldPriorityOrder = getFieldOrder(dtoClass);

        // Ordena los errores de validación según el orden de los campos en el DTO y devuelve solo el primero
        return exception.getFieldErrors().stream()
                .min(Comparator.comparingInt(fieldError -> {
                    int index = fieldPriorityOrder.indexOf(fieldError.getField());
                    return index == -1 ? Integer.MAX_VALUE : index;
                }))
                .map(fieldError -> ResponseEntity.badRequest().body(new ErrorResponse(fieldError.getDefaultMessage())))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // Obtiene los nombres de los campos de un DTO en el orden en que fueron definidos.
    private List<String> getFieldOrder(Class<?> dtoClass) {
        return List.of(dtoClass.getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}

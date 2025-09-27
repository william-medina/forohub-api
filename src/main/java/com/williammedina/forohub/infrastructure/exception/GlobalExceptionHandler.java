package com.williammedina.forohub.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Failed authentication attempt: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Las credenciales proporcionadas son incorrectas.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        log.error("Application error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownHostException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Handles form validation errors (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Field validation error in DTO: {}", exception.getMessage());

        // Gets the class of the DTO associated with the current request
        Class<?> dtoClass = Optional.ofNullable(exception.getBindingResult().getTarget())
                .map(Object::getClass)
                .orElse(null);

        // If the DTO class cannot be determined, return the first error without sorting
        if (dtoClass == null) {
            return exception.getFieldErrors().stream()
                    .findFirst()
                    .map(fieldError -> ResponseEntity.badRequest().body(new ErrorResponse(fieldError.getDefaultMessage())))
                    .orElseGet(() -> ResponseEntity.badRequest().build());
        }

        // Gets the list of field names in the order they were defined in the DTO
        List<String> fieldPriorityOrder = getFieldOrder(dtoClass);

        // Sorts validation errors according to the order of the fields in the DTO and returns only the first one
        return exception.getFieldErrors().stream()
                .min(Comparator.comparingInt(fieldError -> {
                    int index = fieldPriorityOrder.indexOf(fieldError.getField());
                    return index == -1 ? Integer.MAX_VALUE : index;
                }))
                .map(fieldError -> ResponseEntity.badRequest().body(new ErrorResponse(fieldError.getDefaultMessage())))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // Gets the field names of a DTO in the order they were defined
    private List<String> getFieldOrder(Class<?> dtoClass) {
        return Stream.of(dtoClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}

package com.williammedina.forohub.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorResponse buildError(HttpStatus status, String message, String path, Map<String, String> errors) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.name())
                .message(message)
                .errors(errors)
                .path(path)
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Failed authentication attempt: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(HttpStatus.UNAUTHORIZED, "Las credenciales proporcionadas son incorrectas.", request.getRequestURI(), null)
        );
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        log.error("AppException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(
                buildError(ex.getHttpStatus(), ex.getMessage(), request.getRequestURI(), null)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request.getRequestURI(), null)
        );
    }

    // Handles form validation errors (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        log.warn("Field validation error in DTO: {}", exception.getMessage());

        // Gets the class of the DTO associated with the current request
        Class<?> dtoClass = Optional.ofNullable(exception.getBindingResult().getTarget())
                .map(Object::getClass)
                .orElse(null);

        List<FieldError> fieldErrors = exception.getFieldErrors();

        Map<String, String> orderedErrors;

        if (dtoClass != null) {
            // Get the declared field order of the DTO
            List<String> fieldPriorityOrder = getFieldOrder(dtoClass);

            // Sort errors according to the DTO field order
            orderedErrors = fieldErrors.stream()
                    .sorted(Comparator.comparingInt(fieldError -> {
                        int index = fieldPriorityOrder.indexOf(fieldError.getField());
                        return index == -1 ? Integer.MAX_VALUE : index;
                    }))
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("Validation error"),
                            (first, second) -> first,
                            LinkedHashMap::new
                    ));
        } else {
            // Fallback if DTO class is unknown
            orderedErrors = fieldErrors.stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("Validation error"),
                            (first, second) -> first,
                            LinkedHashMap::new
                    ));
        }

        // Take the first error as the main message
        String mainMessage = orderedErrors.isEmpty()
                ? "Validation failed"
                : orderedErrors.values().iterator().next();

        ApiErrorResponse response = buildError(HttpStatus.BAD_REQUEST, mainMessage, request.getRequestURI(), orderedErrors);

        return ResponseEntity.badRequest().body(response);
    }

    // Gets the field names of a DTO in the order they were defined
    private List<String> getFieldOrder(Class<?> dtoClass) {
        return Stream.of(dtoClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}

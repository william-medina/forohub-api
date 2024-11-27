package com.williammedina.forohub.infrastructure.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException{
    private final String message;
    private final String errorCode;
}

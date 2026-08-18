package com.finance.voice.interfaces.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String details,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String message, String details) {
        return new ErrorResponse(message, details, LocalDateTime.now());
    }
}

package com.finance.voice.interfaces.rest.dto;

public record ChatRequest(String message, String sessionId) {
    public ChatRequest {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must not be blank");
        }
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = "default";
        }
    }
}

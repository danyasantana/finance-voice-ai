package com.finance.voice.interfaces.rest.dto;

public record ChatResponse(String response, String sessionId) {
    public static ChatResponse of(String response, String sessionId) {
        return new ChatResponse(response, sessionId);
    }
}

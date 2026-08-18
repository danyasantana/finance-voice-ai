package com.finance.voice.interfaces.rest.dto;

public record ModerationRequest(String text) {
    public ModerationRequest {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text must not be blank");
        }
    }
}

package com.finance.voice.interfaces.rest.dto;

public record VoiceRequest(String text) {
    public VoiceRequest {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text must not be blank");
        }
    }
}

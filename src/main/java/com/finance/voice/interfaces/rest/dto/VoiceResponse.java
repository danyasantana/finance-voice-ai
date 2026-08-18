package com.finance.voice.interfaces.rest.dto;

public record VoiceResponse(String text, String audioBase64) {
    public static VoiceResponse of(String text, String audioBase64) {
        return new VoiceResponse(text, audioBase64);
    }
}

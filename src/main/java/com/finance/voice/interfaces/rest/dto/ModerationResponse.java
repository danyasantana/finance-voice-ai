package com.finance.voice.interfaces.rest.dto;

public record ModerationResponse(boolean approved, String reason, String categories) {
    public static ModerationResponse of(boolean approved, String reason, String categories) {
        return new ModerationResponse(approved, reason, categories);
    }
}

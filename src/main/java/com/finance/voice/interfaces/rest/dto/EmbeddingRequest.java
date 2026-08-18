package com.finance.voice.interfaces.rest.dto;

public record EmbeddingRequest(String query, int topK) {
    public EmbeddingRequest {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query must not be blank");
        }
        if (topK <= 0) {
            topK = 5;
        }
    }
}

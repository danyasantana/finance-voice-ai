package com.finance.voice.interfaces.rest.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        String type,
        BigDecimal amount,
        String description,
        String categoryName
) {
    public TransactionRequest {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type must not be blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

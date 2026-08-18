package com.finance.voice.interfaces.rest.dto;

import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String categoryId,
        TransactionType type,
        Money money,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId().toString(),
                transaction.getCategoryId().toString(),
                transaction.getType(),
                transaction.getMoney(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}

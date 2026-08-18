package com.finance.voice.domain.shared;

import java.util.UUID;

public record TransactionId(UUID value) {

    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId must not be null");
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }

    public static TransactionId of(String value) {
        return new TransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

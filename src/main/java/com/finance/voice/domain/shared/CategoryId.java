package com.finance.voice.domain.shared;

import java.util.UUID;

public record CategoryId(UUID value) {

    public CategoryId {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId of(UUID value) {
        return new CategoryId(value);
    }

    public static CategoryId of(String value) {
        return new CategoryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

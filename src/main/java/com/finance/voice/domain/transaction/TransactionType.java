package com.finance.voice.domain.transaction;

public enum TransactionType {
    INCOME("INCOME", "Receita"),
    EXPENSE("EXPENSE", "Despesa");

    private final String code;
    private final String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionType fromCode(String code) {
        for (TransactionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + code);
    }
}

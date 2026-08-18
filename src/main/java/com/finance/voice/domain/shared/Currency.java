package com.finance.voice.domain.shared;

public enum Currency {
    BRL("BRL", "Real Brasileiro"),
    USD("USD", "Dólar Americano"),
    EUR("EUR", "Euro");

    private final String code;
    private final String description;

    Currency(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}

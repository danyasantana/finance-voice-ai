package com.finance.voice.domain.shared;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidValues() {
        Money money = new Money(BigDecimal.TEN, Currency.BRL);
        assertEquals(BigDecimal.TEN, money.amount());
        assertEquals(Currency.BRL, money.currency());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(null, Currency.BRL));
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-10"), Currency.BRL));
    }

    @Test
    void shouldCreateMoneyWithBrlFactory() {
        Money money = Money.brl(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, money.amount());
        assertEquals(Currency.BRL, money.currency());
    }

    @Test
    void shouldAddMoneySameCurrency() {
        Money money1 = new Money(BigDecimal.TEN, Currency.BRL);
        Money money2 = new Money(new BigDecimal("5"), Currency.BRL);
        Money result = money1.add(money2);
        assertEquals(new BigDecimal("15"), result.amount());
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money money1 = new Money(BigDecimal.TEN, Currency.BRL);
        Money money2 = new Money(BigDecimal.TEN, Currency.USD);
        assertThrows(IllegalArgumentException.class, () -> money1.add(money2));
    }

    @Test
    void shouldSubtractMoney() {
        Money money1 = new Money(new BigDecimal("15"), Currency.BRL);
        Money money2 = new Money(BigDecimal.TEN, Currency.BRL);
        Money result = money1.subtract(money2);
        assertEquals(new BigDecimal("5"), result.amount());
    }

    @Test
    void shouldCompareMoneyGreaterThan() {
        Money money1 = new Money(new BigDecimal("15"), Currency.BRL);
        Money money2 = new Money(BigDecimal.TEN, Currency.BRL);
        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
    }

    @Test
    void shouldCompareMoneyLessThan() {
        Money money1 = new Money(BigDecimal.TEN, Currency.BRL);
        Money money2 = new Money(new BigDecimal("15"), Currency.BRL);
        assertTrue(money1.isLessThan(money2));
        assertFalse(money2.isLessThan(money1));
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        Money money = new Money(new BigDecimal("100.50"), Currency.BRL);
        assertEquals("BRL 100.50", money.toString());
    }
}

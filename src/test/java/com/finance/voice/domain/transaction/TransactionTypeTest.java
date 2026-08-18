package com.finance.voice.domain.transaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

    @Test
    void shouldReturnCorrectDescriptionForExpense() {
        assertEquals("Despesa", TransactionType.EXPENSE.getDescription());
    }

    @Test
    void shouldReturnCorrectDescriptionForIncome() {
        assertEquals("Receita", TransactionType.INCOME.getDescription());
    }

    @Test
    void shouldHaveOnlyTwoValues() {
        assertEquals(2, TransactionType.values().length);
    }
}

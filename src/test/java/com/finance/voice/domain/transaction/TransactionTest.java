package com.finance.voice.domain.transaction;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction createTestTransaction(BigDecimal amount, TransactionType type, String description) {
        return Transaction.create(
                CategoryId.generate(),
                type,
                Money.brl(amount),
                description
        );
    }

    @Test
    void shouldCreateTransactionUsingFactory() {
        CategoryId categoryId = CategoryId.generate();
        Transaction transaction = Transaction.create(categoryId, TransactionType.EXPENSE, Money.brl(new BigDecimal("50")), "Almoço");

        assertNotNull(transaction.getId());
        assertEquals(categoryId, transaction.getCategoryId());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(new BigDecimal("50"), transaction.getMoney().amount());
        assertEquals("Almoço", transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());
    }

    @Test
    void shouldCreateTransactionWithId() {
        TransactionId id = TransactionId.generate();
        CategoryId categoryId = CategoryId.generate();
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = Transaction.withId(id, categoryId, TransactionType.INCOME, Money.brl(new BigDecimal("1000")), "Salário", now, now);

        assertEquals(id, transaction.getId());
        assertEquals(categoryId, transaction.getCategoryId());
        assertEquals("Salário", transaction.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                Transaction.withId(null, CategoryId.generate(), TransactionType.EXPENSE, Money.brl(BigDecimal.TEN), "Test", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenCategoryIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                Transaction.withId(TransactionId.generate(), null, TransactionType.EXPENSE, Money.brl(BigDecimal.TEN), "Test", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThrows(NullPointerException.class, () ->
                Transaction.withId(TransactionId.generate(), CategoryId.generate(), null, Money.brl(BigDecimal.TEN), "Test", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenMoneyIsNull() {
        assertThrows(NullPointerException.class, () ->
                Transaction.withId(TransactionId.generate(), CategoryId.generate(), TransactionType.EXPENSE, null, "Test", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        assertThrows(NullPointerException.class, () ->
                Transaction.withId(TransactionId.generate(), CategoryId.generate(), TransactionType.EXPENSE, Money.brl(BigDecimal.TEN), "Test", null, LocalDateTime.now()));
    }

    @Test
    void shouldBeIncome() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.INCOME, "Salário");
        assertTrue(transaction.isIncome());
        assertFalse(transaction.isExpense());
    }

    @Test
    void shouldBeExpense() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.EXPENSE, "Aluguel");
        assertTrue(transaction.isExpense());
        assertFalse(transaction.isIncome());
    }

    @Test
    void shouldUpdateType() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.EXPENSE, "Test");
        transaction.updateType(TransactionType.INCOME);
        assertEquals(TransactionType.INCOME, transaction.getType());
    }

    @Test
    void shouldUpdateMoney() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.EXPENSE, "Test");
        transaction.updateMoney(Money.brl(new BigDecimal("20")));
        assertEquals(new BigDecimal("20"), transaction.getMoney().amount());
    }

    @Test
    void shouldUpdateDescription() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.EXPENSE, "Old");
        transaction.updateDescription("New");
        assertEquals("New", transaction.getDescription());
    }

    @Test
    void shouldUpdateCategoryId() {
        Transaction transaction = createTestTransaction(BigDecimal.TEN, TransactionType.EXPENSE, "Test");
        CategoryId newCategoryId = CategoryId.generate();
        transaction.updateCategoryId(newCategoryId);
        assertEquals(newCategoryId, transaction.getCategoryId());
    }
}

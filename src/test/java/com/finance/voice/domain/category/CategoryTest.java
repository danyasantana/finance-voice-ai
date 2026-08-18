package com.finance.voice.domain.category;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.transaction.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategoryUsingFactory() {
        Category category = Category.create("Alimentação", TransactionType.EXPENSE);
        assertNotNull(category.getId());
        assertEquals("Alimentação", category.getName());
        assertEquals(TransactionType.EXPENSE, category.getType());
    }

    @Test
    void shouldCreateCategoryWithId() {
        CategoryId id = CategoryId.generate();
        Category category = Category.withId(id, "Salário", TransactionType.INCOME);
        assertEquals(id, category.getId());
        assertEquals("Salário", category.getName());
        assertEquals(TransactionType.INCOME, category.getType());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullPointerException.class,
                () -> Category.create(null, TransactionType.EXPENSE));
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThrows(NullPointerException.class,
                () -> Category.create("Food", null));
    }

    @Test
    void shouldUpdateName() {
        Category category = Category.create("Food", TransactionType.EXPENSE);
        category.updateName("Alimentação");
        assertEquals("Alimentação", category.getName());
    }

    @Test
    void shouldUpdateType() {
        Category category = Category.create("Food", TransactionType.EXPENSE);
        category.updateType(TransactionType.INCOME);
        assertEquals(TransactionType.INCOME, category.getType());
    }

    @Test
    void shouldBeEqualByCategoryId() {
        CategoryId id = CategoryId.generate();
        Category c1 = Category.withId(id, "Food", TransactionType.EXPENSE);
        Category c2 = Category.withId(id, "Transport", TransactionType.INCOME);
        assertEquals(c1, c2);
    }
}

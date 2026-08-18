package com.finance.voice.domain.transaction;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.TransactionId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private final TransactionId id;
    private CategoryId categoryId;
    private TransactionType type;
    private Money money;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Transaction(TransactionId id, CategoryId categoryId, TransactionType type,
                        Money money, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Id must not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "CategoryId must not be null");
        this.type = Objects.requireNonNull(type, "Type must not be null");
        this.money = Objects.requireNonNull(money, "Money must not be null");
        this.description = description;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt must not be null");
    }

    public static Transaction create(CategoryId categoryId, TransactionType type,
                                     Money money, String description) {
        LocalDateTime now = LocalDateTime.now();
        return new Transaction(
                TransactionId.generate(),
                categoryId,
                type,
                money,
                description,
                now,
                now
        );
    }

    public static Transaction withId(TransactionId id, CategoryId categoryId, TransactionType type,
                                     Money money, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Transaction(id, categoryId, type, money, description, createdAt, updatedAt);
    }

    public TransactionId getId() {
        return id;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getMoney() {
        return money;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Type must not be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMoney(Money money) {
        this.money = Objects.requireNonNull(money, "Money must not be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCategoryId(CategoryId categoryId) {
        this.categoryId = Objects.requireNonNull(categoryId, "CategoryId must not be null");
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isIncome() {
        return this.type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return this.type == TransactionType.EXPENSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", type=" + type +
                ", money=" + money +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

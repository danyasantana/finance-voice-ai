package com.finance.voice.domain.category;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.transaction.TransactionType;

import java.util.Objects;

public class Category {

    private final CategoryId id;
    private String name;
    private TransactionType type;

    private Category(CategoryId id, String name, TransactionType type) {
        this.id = Objects.requireNonNull(id, "Id must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.type = Objects.requireNonNull(type, "Type must not be null");
    }

    public static Category create(String name, TransactionType type) {
        return new Category(CategoryId.generate(), name, type);
    }

    public static Category withId(CategoryId id, String name, TransactionType type) {
        return new Category(id, name, type);
    }

    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TransactionType getType() {
        return type;
    }

    public void updateName(String name) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
    }

    public void updateType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Type must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

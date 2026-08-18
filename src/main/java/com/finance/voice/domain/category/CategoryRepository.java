package com.finance.voice.domain.category;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.transaction.TransactionType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(CategoryId id);

    List<Category> findAll();

    List<Category> findByType(TransactionType type);

    void deleteById(CategoryId id);

    boolean existsById(CategoryId id);
}

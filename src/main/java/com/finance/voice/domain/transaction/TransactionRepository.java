package com.finance.voice.domain.transaction;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.TransactionId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findAll();

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategoryId(CategoryId categoryId);

    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction> findByDescriptionContaining(String description);

    void deleteById(TransactionId id);

    boolean existsById(TransactionId id);
}

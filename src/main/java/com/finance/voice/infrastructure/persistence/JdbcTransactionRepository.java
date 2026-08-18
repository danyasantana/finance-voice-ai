package com.finance.voice.infrastructure.persistence;

import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.Currency;
import com.finance.voice.domain.shared.TransactionId;
import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionRepository;
import com.finance.voice.domain.transaction.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> rowMapper = (rs, rowNum) -> {
        TransactionId id = TransactionId.of(rs.getObject("id", UUID.class));
        CategoryId categoryId = CategoryId.of(rs.getObject("category_id", UUID.class));
        TransactionType type = TransactionType.fromCode(rs.getString("type"));
        BigDecimal amount = rs.getBigDecimal("amount");
        Currency currency = Currency.fromCode(rs.getString("currency"));
        String description = rs.getString("description");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

        Money money = new Money(amount, currency);
        return Transaction.withId(id, categoryId, type, money, description, createdAt, updatedAt);
    };

    public JdbcTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions (id, category_id, type, amount, currency, description, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    category_id = EXCLUDED.category_id,
                    type = EXCLUDED.type,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    description = EXCLUDED.description,
                    updated_at = EXCLUDED.updated_at
                """;

        jdbcTemplate.update(sql,
                transaction.getId().value(),
                transaction.getCategoryId().value(),
                transaction.getType().getCode(),
                transaction.getMoney().amount(),
                transaction.getMoney().currency().getCode(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

        return transaction;
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        List<Transaction> results = jdbcTemplate.query(sql, rowMapper, id.value());
        return results.stream().findFirst();
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        String sql = "SELECT * FROM transactions WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, type.getCode());
    }

    @Override
    public List<Transaction> findByCategoryId(CategoryId categoryId) {
        String sql = "SELECT * FROM transactions WHERE category_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, categoryId.value());
    }

    @Override
    public List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM transactions WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, start, end);
    }

    @Override
    public List<Transaction> findByDescriptionContaining(String description) {
        String sql = "SELECT * FROM transactions WHERE description ILIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, "%" + description + "%");
    }

    @Override
    public void deleteById(TransactionId id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        jdbcTemplate.update(sql, id.value());
    }

    @Override
    public boolean existsById(TransactionId id) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.value());
        return count != null && count > 0;
    }
}

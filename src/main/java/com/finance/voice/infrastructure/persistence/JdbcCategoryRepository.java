package com.finance.voice.infrastructure.persistence;

import com.finance.voice.domain.category.Category;
import com.finance.voice.domain.category.CategoryRepository;
import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.transaction.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcCategoryRepository implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
        CategoryId id = CategoryId.of(rs.getObject("id", UUID.class));
        String name = rs.getString("name");
        TransactionType type = TransactionType.fromCode(rs.getString("type"));
        return Category.withId(id, name, type);
    };

    public JdbcCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category save(Category category) {
        String sql = """
                INSERT INTO categories (id, name, type)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    type = EXCLUDED.type
                """;

        jdbcTemplate.update(sql,
                category.getId().value(),
                category.getName(),
                category.getType().getCode()
        );

        return category;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        List<Category> results = jdbcTemplate.query(sql, rowMapper, id.value());
        return results.stream().findFirst();
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories ORDER BY name";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Category> findByType(TransactionType type) {
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        return jdbcTemplate.query(sql, rowMapper, type.getCode());
    }

    @Override
    public void deleteById(CategoryId id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id.value());
    }

    @Override
    public boolean existsById(CategoryId id) {
        String sql = "SELECT COUNT(*) FROM categories WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.value());
        return count != null && count > 0;
    }
}

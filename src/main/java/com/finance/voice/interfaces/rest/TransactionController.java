package com.finance.voice.interfaces.rest;

import com.finance.voice.domain.category.Category;
import com.finance.voice.domain.category.CategoryRepository;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.TransactionId;
import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionRepository;
import com.finance.voice.domain.transaction.TransactionType;
import com.finance.voice.interfaces.rest.dto.ErrorResponse;
import com.finance.voice.interfaces.rest.dto.TransactionRequest;
import com.finance.voice.interfaces.rest.dto.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        log.info("Finding all transactions");
        List<TransactionResponse> transactions = transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable String id) {
        log.info("Finding transaction by id: {}", id);
        return transactionRepository.findById(TransactionId.of(id))
                .map(TransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest request) {
        log.info("Creating transaction: {}", request);

        TransactionType type = TransactionType.fromCode(request.type());
        Category category = findOrCreateCategory(request.categoryName(), type);
        Money money = Money.brl(request.amount());

        Transaction transaction = Transaction.create(
                category.getId(),
                type,
                money,
                request.description()
        );

        Transaction saved = transactionRepository.save(transaction);
        return ResponseEntity.ok(TransactionResponse.from(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Deleting transaction: {}", id);
        TransactionId transactionId = TransactionId.of(id);

        if (!transactionRepository.existsById(transactionId)) {
            return ResponseEntity.notFound().build();
        }

        transactionRepository.deleteById(transactionId);
        return ResponseEntity.noContent().build();
    }

    private Category findOrCreateCategory(String name, TransactionType type) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name) && c.getType() == type)
                .findFirst()
                .orElseGet(() -> {
                    Category newCategory = Category.create(name, type);
                    return categoryRepository.save(newCategory);
                });
    }
}

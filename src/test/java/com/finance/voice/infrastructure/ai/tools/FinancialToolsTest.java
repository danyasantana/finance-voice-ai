package com.finance.voice.infrastructure.ai.tools;

import com.finance.voice.domain.category.Category;
import com.finance.voice.domain.category.CategoryRepository;
import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.TransactionId;
import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionRepository;
import com.finance.voice.domain.transaction.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialToolsTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FinancialTools financialTools;

    private Transaction createTestTransaction(BigDecimal amount, TransactionType type, String description) {
        return Transaction.create(
                CategoryId.generate(),
                type,
                Money.brl(amount),
                description
        );
    }

    @Test
    void shouldRegisterTransaction() {
        CategoryId categoryId = CategoryId.generate();
        Category category = Category.withId(categoryId, "Alimentação", TransactionType.EXPENSE);
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        String result = financialTools.registerTransaction(
                "EXPENSE",
                new BigDecimal("50.00"),
                "Almoço",
                "Alimentação"
        );

        assertNotNull(result);
        assertTrue(result.contains("Transação registrada com sucesso"));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldCheckBalance() {
        Transaction income = createTestTransaction(new BigDecimal("1000"), TransactionType.INCOME, "Salário");
        Transaction expense = createTestTransaction(new BigDecimal("500"), TransactionType.EXPENSE, "Aluguel");
        when(transactionRepository.findAll()).thenReturn(List.of(income, expense));

        String result = financialTools.checkBalance();

        assertTrue(result.contains("Saldo atual"));
        assertTrue(result.contains("Receitas: R$ 1000"));
        assertTrue(result.contains("Despesas: R$ 500"));
    }

    @Test
    void shouldListTransactions() {
        Transaction transaction = createTestTransaction(new BigDecimal("100"), TransactionType.EXPENSE, "Café");
        when(transactionRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(transaction));

        String result = financialTools.listTransactions("2024-01-01", "2024-01-31");

        assertTrue(result.contains("Transações no período"));
        assertTrue(result.contains("Café"));
    }

    @Test
    void shouldReturnEmptyMessageWhenNoTransactions() {
        when(transactionRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        String result = financialTools.listTransactions("2024-01-01", "2024-01-31");

        assertTrue(result.contains("Nenhuma transação encontrada"));
    }

    @Test
    void shouldGetMonthlySummary() {
        Transaction income = createTestTransaction(new BigDecimal("5000"), TransactionType.INCOME, "Salário");
        Transaction expense = createTestTransaction(new BigDecimal("1000"), TransactionType.EXPENSE, "Aluguel");
        when(transactionRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(income, expense));

        String result = financialTools.getMonthlySummary("2024-01");

        assertTrue(result.contains("Resumo financeiro de 2024-01"));
        assertTrue(result.contains("Receitas: R$ 5000"));
        assertTrue(result.contains("Despesas: R$ 1000"));
    }

    @Test
    void shouldDeleteTransaction() {
        TransactionId transactionId = TransactionId.generate();
        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        String result = financialTools.deleteTransaction(transactionId.toString());

        assertTrue(result.contains("Transação removida com sucesso"));
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void shouldReturnErrorMessageWhenDeletingNonExistentTransaction() {
        TransactionId transactionId = TransactionId.generate();
        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        String result = financialTools.deleteTransaction(transactionId.toString());

        assertTrue(result.contains("Transação não encontrada"));
        verify(transactionRepository, never()).deleteById(any());
    }
}

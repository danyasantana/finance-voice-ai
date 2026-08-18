package com.finance.voice.infrastructure.ai.tools;

import com.finance.voice.domain.category.Category;
import com.finance.voice.domain.category.CategoryRepository;
import com.finance.voice.domain.shared.CategoryId;
import com.finance.voice.domain.shared.Money;
import com.finance.voice.domain.shared.TransactionId;
import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionRepository;
import com.finance.voice.domain.transaction.TransactionType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FinancialTools {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public FinancialTools(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Tool(description = "Registra uma transação financeira (receita ou despesa)")
    public String registerTransaction(
            @ToolParam(description = "Tipo da transação: INCOME para receita, EXPENSE para despesa") String type,
            @ToolParam(description = "Valor da transação em reais") BigDecimal amount,
            @ToolParam(description = "Descrição da transação") String description,
            @ToolParam(description = "Nome da categoria (ex: Alimentação, Transporte, Salário)") String categoryName) {

        TransactionType transactionType = TransactionType.fromCode(type);
        Category category = findOrCreateCategory(categoryName, transactionType);
        Money money = Money.brl(amount);

        Transaction transaction = Transaction.create(
                category.getId(),
                transactionType,
                money,
                description
        );

        transactionRepository.save(transaction);

        return String.format("Transação registrada com sucesso! ID: %s, Tipo: %s, Valor: R$ %s, Categoria: %s",
                transaction.getId(), transactionType.getDescription(), amount.toPlainString(), categoryName);
    }

    @Tool(description = "Consulta o saldo total (receitas - despesas)")
    public String checkBalance() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        BigDecimal totalIncome = allTransactions.stream()
                .filter(Transaction::isIncome)
                .map(t -> t.getMoney().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = allTransactions.stream()
                .filter(Transaction::isExpense)
                .map(t -> t.getMoney().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return String.format("Saldo atual: R$ %s\nReceitas: R$ %s\nDespesas: R$ %s",
                balance.toPlainString(), totalIncome.toPlainString(), totalExpense.toPlainString());
    }

    @Tool(description = "Lista transações por período (data inicial e final no formato AAAA-MM-DD)")
    public String listTransactions(
            @ToolParam(description = "Data inicial no formato AAAA-MM-DD") String startDate,
            @ToolParam(description = "Data final no formato AAAA-MM-DD") String endDate) {

        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);

        if (transactions.isEmpty()) {
            return "Nenhuma transação encontrada no período informado.";
        }

        String result = transactions.stream()
                .map(t -> String.format("- %s: R$ %s (%s) - %s",
                        t.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        t.getMoney().amount().toPlainString(),
                        t.getType().getDescription(),
                        t.getDescription() != null ? t.getDescription() : "Sem descrição"))
                .collect(Collectors.joining("\n"));

        return String.format("Transações no período %s a %s:\n%s", startDate, endDate, result);
    }

    @Tool(description = "Retorna resumo mensal de gastos por categoria")
    public String getMonthlySummary(
            @ToolParam(description = "Mês no formato AAAA-MM (ex: 2024-01)") String month) {

        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);

        if (transactions.isEmpty()) {
            return "Nenhuma transação encontrada no mês informado.";
        }

        // Group expenses by category
        String expensesByCategory = transactions.stream()
                .filter(Transaction::isExpense)
                .collect(Collectors.groupingBy(
                        t -> findCategoryName(t.getCategoryId()),
                        Collectors.reducing(BigDecimal.ZERO, t -> t.getMoney().amount(), BigDecimal::add)
                ))
                .entrySet().stream()
                .map(entry -> String.format("- %s: R$ %s", entry.getKey(), entry.getValue().toPlainString()))
                .collect(Collectors.joining("\n"));

        // Calculate totals
        BigDecimal totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .map(t -> t.getMoney().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(Transaction::isExpense)
                .map(t -> t.getMoney().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format("Resumo financeiro de %s:\n\nReceitas: R$ %s\nDespesas: R$ %s\nSaldo: R$ %s\n\nDespesas por categoria:\n%s",
                month, totalIncome.toPlainString(), totalExpense.toPlainString(),
                totalIncome.subtract(totalExpense).toPlainString(),
                expensesByCategory.isEmpty() ? "Nenhuma despesa registrada" : expensesByCategory);
    }

    @Tool(description = "Remove uma transação pelo ID")
    public String deleteTransaction(
            @ToolParam(description = "ID da transação a ser removida") String id) {

        TransactionId transactionId = TransactionId.of(id);

        if (!transactionRepository.existsById(transactionId)) {
            return "Transação não encontrada.";
        }

        transactionRepository.deleteById(transactionId);
        return "Transação removida com sucesso!";
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

    private String findCategoryName(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Categoria Desconhecida");
    }
}

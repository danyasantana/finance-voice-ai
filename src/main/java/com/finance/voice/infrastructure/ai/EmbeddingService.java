package com.finance.voice.infrastructure.ai;

import com.finance.voice.domain.transaction.Transaction;
import com.finance.voice.domain.transaction.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    private final EmbeddingModel embeddingModel;
    private final TransactionRepository transactionRepository;

    public EmbeddingService(EmbeddingModel embeddingModel, TransactionRepository transactionRepository) {
        this.embeddingModel = embeddingModel;
        this.transactionRepository = transactionRepository;
    }

    public List<Map<String, Object>> searchBySemantic(String query, int topK) {
        log.info("Performing semantic search for: {}", query);

        List<Transaction> allTransactions = transactionRepository.findAll();

        if (allTransactions.isEmpty()) {
            return List.of();
        }

        float[] queryEmbedding = embeddingModel.embed(query);

        List<TransactionScore> scoredTransactions = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            String transactionText = buildTransactionText(transaction);
            float[] transactionEmbedding = embeddingModel.embed(transactionText);

            double similarity = cosineSimilarity(queryEmbedding, transactionEmbedding);
            scoredTransactions.add(new TransactionScore(transaction, similarity));
        }

        return scoredTransactions.stream()
                .sorted(Comparator.comparingDouble(TransactionScore::score).reversed())
                .limit(topK)
                .map(ts -> Map.<String, Object>of(
                        "transaction", ts.transaction(),
                        "score", ts.score()
                ))
                .collect(Collectors.toList());
    }

    private String buildTransactionText(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append(transaction.getType().getDescription());
        sb.append(" de R$ ").append(transaction.getMoney().amount().toPlainString());
        if (transaction.getDescription() != null) {
            sb.append(" - ").append(transaction.getDescription());
        }
        return sb.toString();
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private record TransactionScore(Transaction transaction, double score) {}
}

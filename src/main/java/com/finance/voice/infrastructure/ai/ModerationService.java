package com.finance.voice.infrastructure.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

    private static final Logger log = LoggerFactory.getLogger(ModerationService.class);

    private final ChatClient chatClient;

    public ModerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ModerationResult moderate(String content) {
        log.info("Moderating content: {}", content);

        String prompt = String.format("""
                Analise o seguinte conteúdo e retorne um JSON com:
                - "approved": true se o conteúdo for apropriado, false caso contrário
                - "reason": motivo da rejeição (se aplicável)
                - "categories": lista de categorias de violação detectadas (se houver)
                
                Categorias possíveis: hate, harassment, violence, self-harm, sexual, spam
                
                Conteúdo para analisar: "%s"
                
                Responda APENAS com o JSON, sem texto adicional.
                """, content);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseModerationResult(response);
    }

    private ModerationResult parseModerationResult(String response) {
        try {
            // Simple parsing - in production, use a proper JSON parser
            boolean approved = response.contains("\"approved\": true") || response.contains("\"approved\":true");
            String reason = extractValue(response, "reason");
            String categoriesStr = extractValue(response, "categories");

            return new ModerationResult(approved, reason, categoriesStr);
        } catch (Exception e) {
            log.error("Error parsing moderation response: {}", response, e);
            return new ModerationResult(true, "Erro na moderação", "");
        }
    }

    private String extractValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) return "";

        int colonIndex = json.indexOf(":", keyIndex);
        int startQuote = json.indexOf("\"", colonIndex + 1);
        int endQuote = json.indexOf("\"", startQuote + 1);

        if (startQuote == -1 || endQuote == -1) return "";

        return json.substring(startQuote + 1, endQuote);
    }

    public record ModerationResult(boolean approved, String reason, String categories) {
        public boolean isApproved() {
            return approved;
        }
    }
}

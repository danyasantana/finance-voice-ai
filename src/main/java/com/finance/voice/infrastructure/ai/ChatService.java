package com.finance.voice.infrastructure.ai;

import com.finance.voice.infrastructure.ai.tools.FinancialTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final Map<String, MessageWindowChatMemory> chatMemories = new ConcurrentHashMap<>();

    public ChatService(ChatClient.Builder chatClientBuilder, FinancialTools financialTools) {
        this.chatClient = chatClientBuilder
                .defaultTools(financialTools)
                .build();
    }

    public String chat(String sessionId, String message) {
        log.info("Processing chat message for session {}: {}", sessionId, message);

        MessageWindowChatMemory chatMemory = chatMemories.computeIfAbsent(sessionId,
                k -> MessageWindowChatMemory.builder()
                        .chatMemoryRepository(new InMemoryChatMemoryRepository())
                        .maxMessages(10)
                        .build());

        MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(sessionId)
                .build();

        String response = chatClient.prompt()
                .advisors(advisor)
                .user(message)
                .call()
                .content();

        log.info("Chat response for session {}: {}", sessionId, response);
        return response;
    }

    public void clearSession(String sessionId) {
        chatMemories.remove(sessionId);
        log.info("Chat session cleared: {}", sessionId);
    }
}

package com.finance.voice.infrastructure.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private ModerationService moderationService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        moderationService = new ModerationService(chatClientBuilder);
    }

    private void mockModerationChain(String response) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(response);
    }

    @Test
    void shouldApproveAppropriateContent() {
        mockModerationChain("{\"approved\": true, \"reason\": \"\", \"categories\": []}");

        ModerationService.ModerationResult result = moderationService.moderate("Olá, tudo bem?");

        assertTrue(result.isApproved());
    }

    @Test
    void shouldRejectInappropriateContent() {
        mockModerationChain("{\"approved\": false, \"reason\": \"Conteúdo inadequado\", \"categories\": [\"hate\"]}");

        ModerationService.ModerationResult result = moderationService.moderate("Mensagem de ódio");

        assertFalse(result.isApproved());
        assertEquals("Conteúdo inadequado", result.reason());
    }

    @Test
    void shouldHandleInvalidJsonResponse() {
        mockModerationChain("Resposta inválida");

        ModerationService.ModerationResult result = moderationService.moderate("Teste");

        assertFalse(result.isApproved());
        assertEquals("", result.reason());
    }

    @Test
    void shouldExtractCategoriesFromResponse() {
        mockModerationChain("{\"approved\": false, \"reason\": \"Spam\", \"categories\": [\"spam\"]}");

        ModerationService.ModerationResult result = moderationService.moderate("Mensagem de spam");

        assertFalse(result.isApproved());
        assertEquals("spam", result.categories());
    }
}

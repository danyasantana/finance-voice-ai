package com.finance.voice.interfaces.rest;

import com.finance.voice.infrastructure.ai.ChatService;
import com.finance.voice.infrastructure.ai.ModerationService;
import com.finance.voice.interfaces.rest.dto.ErrorResponse;
import com.finance.voice.interfaces.rest.dto.VoiceRequest;
import com.finance.voice.interfaces.rest.dto.VoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voice")
@CrossOrigin(origins = "*")
public class VoiceController {

    private static final Logger log = LoggerFactory.getLogger(VoiceController.class);

    private final ChatService chatService;
    private final ModerationService moderationService;

    public VoiceController(ChatService chatService, ModerationService moderationService) {
        this.chatService = chatService;
        this.moderationService = moderationService;
    }

    @PostMapping("/command")
    public ResponseEntity<?> processVoiceCommand(@RequestBody VoiceRequest request) {
        log.info("Processing voice command: {}", request.text());

        try {
            // Step 1: Moderate the input
            ModerationService.ModerationResult moderationResult = moderationService.moderate(request.text());
            if (!moderationResult.isApproved()) {
                log.warn("Content rejected by moderation: {}", moderationResult.reason());
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.of("Conteúdo rejeitado", moderationResult.reason()));
            }

            // Step 2: Process with Chat (includes Tool Calling)
            String sessionId = "voice-" + System.currentTimeMillis();
            String chatResponse = chatService.chat(sessionId, request.text());

            // Step 3: Return response (TTS is handled by frontend)
            return ResponseEntity.ok(VoiceResponse.of(chatResponse, null));

        } catch (Exception e) {
            log.error("Error processing voice command", e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Erro ao processar comando", e.getMessage()));
        }
    }

    @PostMapping("/text-to-speech")
    public ResponseEntity<VoiceResponse> textToSpeech(@RequestBody VoiceRequest request) {
        log.info("Text to speech request: {}", request.text());

        // TTS is handled by frontend using Web Speech API
        // This endpoint is for future backend TTS implementation
        return ResponseEntity.ok(VoiceResponse.of(request.text(), null));
    }
}

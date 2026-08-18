package com.finance.voice.interfaces.rest;

import com.finance.voice.infrastructure.ai.ChatService;
import com.finance.voice.interfaces.rest.dto.ChatRequest;
import com.finance.voice.interfaces.rest.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Chat request: {}", request);

        String response = chatService.chat(request.sessionId(), request.message());
        ChatResponse chatResponse = ChatResponse.of(response, request.sessionId());

        return ResponseEntity.ok(chatResponse);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        log.info("Clearing session: {}", sessionId);
        chatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}

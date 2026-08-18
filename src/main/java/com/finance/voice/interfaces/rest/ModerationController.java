package com.finance.voice.interfaces.rest;

import com.finance.voice.infrastructure.ai.ModerationService;
import com.finance.voice.interfaces.rest.dto.ErrorResponse;
import com.finance.voice.interfaces.rest.dto.ModerationRequest;
import com.finance.voice.interfaces.rest.dto.ModerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moderation")
@CrossOrigin(origins = "*")
public class ModerationController {

    private static final Logger log = LoggerFactory.getLogger(ModerationController.class);

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody ModerationRequest request) {
        log.info("Moderation check request: {}", request.text());

        try {
            ModerationService.ModerationResult result = moderationService.moderate(request.text());
            ModerationResponse response = ModerationResponse.of(
                    result.approved(),
                    result.reason(),
                    result.categories()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error performing moderation", e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Erro na moderação", e.getMessage()));
        }
    }
}

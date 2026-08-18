package com.finance.voice.interfaces.rest;

import com.finance.voice.infrastructure.ai.EmbeddingService;
import com.finance.voice.interfaces.rest.dto.EmbeddingRequest;
import com.finance.voice.interfaces.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/embedding")
@CrossOrigin(origins = "*")
public class EmbeddingController {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingController.class);

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody EmbeddingRequest request) {
        log.info("Embedding search request: {} (topK: {})", request.query(), request.topK());

        try {
            List<Map<String, Object>> results = embeddingService.searchBySemantic(request.query(), request.topK());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error performing embedding search", e);
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.of("Erro na busca semântica", e.getMessage()));
        }
    }
}

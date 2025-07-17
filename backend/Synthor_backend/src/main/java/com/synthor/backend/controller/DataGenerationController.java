package com.synthor.backend.controller;

import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.service.DataGenerationService;
import com.synthor.backend.service.NlpService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataGenerationController {

    private final DataGenerationService dataGenerationService;
    private final NlpService nlpService;

    public DataGenerationController(DataGenerationService dataGenerationService, NlpService nlpService) {
        this.dataGenerationService = dataGenerationService;
        this.nlpService = nlpService;
    }

    @PostMapping("/manual-generate")
    public List<Map<String, Object>> manualGenerateData(@RequestBody DataGenerationRequest request) {
        return dataGenerationService.generateData(request);
    }

    @PostMapping("/ai-generate")
    public List<Map<String, Object>> aiGenerateData(@RequestBody String query) {
        // 1. Parse the natural language query using the NLP service
        DataGenerationRequest request = nlpService.parseQuery(query);

        // 2. Generate data using the existing data generation service
        return dataGenerationService.generateData(request);
    }
}

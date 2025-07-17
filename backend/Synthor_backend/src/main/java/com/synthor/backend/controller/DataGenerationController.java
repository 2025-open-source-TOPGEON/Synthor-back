package com.synthor.backend.controller;

import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.service.DataGenerationService;
import com.synthor.backend.service.NlpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "수동 데이터 생성",
            description = "지정된 필드 타입과 개수에 따라 가짜 데이터를 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "데이터 생성 요청 명세",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataGenerationRequest.class),
                            examples = @ExampleObject(
                                    name = "기본 예시",
                                    value = """
                                            {
                                              "count": 5,
                                              "fields": [
                                                {
                                                  "name": "userEmail",
                                                  "type": "email_address"
                                                },
                                                {
                                                  "name": "userName",
                                                  "type": "full_name"
                                                },
                                                {
                                                  "name": "company",
                                                  "type": "company_name"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    )
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

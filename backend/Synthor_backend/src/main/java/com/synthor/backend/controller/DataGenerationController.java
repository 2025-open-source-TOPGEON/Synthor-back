package com.synthor.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.dto.DataGenerationResponse;
import com.synthor.backend.service.DataGenerationService;
import com.synthor.backend.service.DataFormattingService;
import com.synthor.backend.service.NlpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataGenerationController {

    private final DataGenerationService dataGenerationService;
    private final DataFormattingService dataFormattingService;
    private final NlpService nlpService;

    public DataGenerationController(DataGenerationService dataGenerationService, DataFormattingService dataFormattingService, NlpService nlpService) {
        this.dataGenerationService = dataGenerationService;
        this.dataFormattingService = dataFormattingService;
        this.nlpService = nlpService;
    }

    @Operation(
            summary = "수동 데이터 생성",
            description = "지정된 필드 타입과 개수에 따라 가짜 데이터를 생성하고, 적용된 필드 정보와 함께 반환합니다.",
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
                                              "count": 100,
                                              "fields": [
                                                {
                                                  "name": "userPassword",
                                                  "prompt": "타입을 full name 으로 바꿔줘",
                                                  "type": "password",
                                                  "constraints": {
                                                    "minimum_length": 12,
                                                    "upper": 1,
                                                    "lower": 1,
                                                    "numbers": 1,
                                                    "symbols": 1
                                                  },
                                                  "nullablePercent": 50
                                                },
                                                {
                                                  "name": "userEmail",
                                                  "prompt": "비밀번호는 최소 10자 이상이고 숫자와 특수문자가 포함되어야 해",
                                                  "type": "email_address",
                                                  "constraints": {},
                                                  "nullablePercent": 0
                                                },
                                                {
                                                  "name": "real_userEmail",
                                                  "prompt": "",
                                                  "type": "email_address",
                                                  "constraints": {},
                                                  "nullablePercent": 50
                                                },
                                                {
                                                  "name": "userCountry",
                                                  "type": "country",
                                                  "prompt": "이건 ai 가 절대 해석 못할거임.(오픈소스 우승은 synthor)",
                                                  "constraints": {
                                                    "options": [
                                                      "South Korea",
                                                      "USA",
                                                      "Japan",
                                                      "China"
                                                    ]
                                                  },
                                                  "nullablePercent": 30
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/manual-generate")
    public ResponseEntity<DataGenerationResponse> manualGenerateData(
            @RequestBody DataGenerationRequest request) {

        // 1. Generate the data and get the comprehensive response object
        DataGenerationResponse response = dataGenerationService.generateData(request);

        // 2. Return the response object directly
        // The object will be automatically serialized to JSON
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ai-generate")
    public ResponseEntity<DataGenerationResponse> aiGenerateData(
            @RequestBody String query) {
        // 1. Parse the natural language query using the NLP service
        DataGenerationRequest request = nlpService.parseQuery(query);

        // 2. Generate data using the existing data generation service
        DataGenerationResponse response = dataGenerationService.generateData(request);

        // 3. Return the response object directly
        return ResponseEntity.ok(response);
    }

    /**
     * Determines the correct HTTP Content-Type based on the requested format.
     * @param format The format string (e.g., "json", "csv").
     * @return The corresponding MediaType.
     */
    private MediaType getContentTypeForFormat(String format) {
        return switch (format.toLowerCase()) {
            case "csv" -> MediaType.valueOf("text/csv");
            case "html" -> MediaType.TEXT_HTML;
            case "sql" -> MediaType.valueOf("application/sql");
            case "xml" -> MediaType.APPLICATION_XML;
            case "ldif" -> MediaType.valueOf("text/plain"); // LDIF is often treated as plain text
            case "json" -> MediaType.APPLICATION_JSON;
            default -> MediaType.APPLICATION_JSON;
        };
    }
}
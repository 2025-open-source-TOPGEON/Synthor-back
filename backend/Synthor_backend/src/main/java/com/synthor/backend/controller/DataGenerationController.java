package com.synthor.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synthor.backend.dto.AiApiAutoGenerateResponse;
import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.service.AiApiService;
import com.synthor.backend.service.DataGenerationService;
import com.synthor.backend.service.DataFormattingService;
import com.synthor.backend.service.NlpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
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
    private final AiApiService aiApiService; // AiApiService 주입

    public DataGenerationController(DataGenerationService dataGenerationService, DataFormattingService dataFormattingService, NlpService nlpService, AiApiService aiApiService) {
        this.dataGenerationService = dataGenerationService;
        this.dataFormattingService = dataFormattingService;
        this.nlpService = nlpService;
        this.aiApiService = aiApiService; // 생성자에서 초기화
    }

    @Operation(
            summary = "수동 데이터 생성",
            description = "지정된 필드 타입과 개수에 따라 가짜 데이터를 생성하고, 요청된 포맷으로 반환합니다.",
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
    public ResponseEntity<String> manualGenerateData(
            @RequestBody DataGenerationRequest request,
            @Parameter(description = "반환받을 데이터 포맷 (json, csv, html, sql, xml, ldif)", example = "json")
            @RequestParam(defaultValue = "json") String format) throws JsonProcessingException {

        // 1. Generate the core data
        List<Map<String, Object>> generatedData = dataGenerationService.generateData(request);

        // 2. Format the data into the requested format
        String formattedData = dataFormattingService.format(generatedData, format);

        // 3. Return the response with the appropriate content type
        MediaType contentType = getContentTypeForFormat(format);
        return ResponseEntity.ok().contentType(contentType).body(formattedData);
    }

    @Operation(
            summary = "AI 기반 필드 자동 생성",
            description = "자연어 프롬프트를 기반으로 데이터 필드 정의(스키마)를 자동으로 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "필드 생성을 위한 자연어 프롬프트",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "쇼핑몰 예시",
                                    value = "\"쇼핑몰에서 사용자 등록을 위한 정보\""
                            )
                    )
            )
    )
    @PostMapping("/ai-generate")
    public ResponseEntity<Object> aiGenerateData(@RequestBody String prompt) {
        // AI 서비스를 호출하여 필드 정의를 가져옵니다.
        AiApiAutoGenerateResponse response = aiApiService.autoGenerateFieldsFromPrompt(prompt);

        if (response == null) {
            // AI API 호출 실패 시 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get response from AI service.");
        }

        // 성공 시 AI API의 응답을 그대로 클라이언트에게 반환합니다.
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

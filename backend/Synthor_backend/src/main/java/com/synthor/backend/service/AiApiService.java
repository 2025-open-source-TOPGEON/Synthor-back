package com.synthor.backend.service;

import com.synthor.backend.dto.AiApiRequest;
import com.synthor.backend.dto.AiApiResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiApiService {

    private final WebClient webClient;

    // Spring Boot가 자동으로 WebClient.Builder를 주입해줍니다.
    public AiApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://synthor-ai.onrender.com").build();
    }

    public AiApiResponse suggestFieldType(String prompt) {
        AiApiRequest requestBody = new AiApiRequest(prompt);

        try {
            // API를 호출하고 응답을 AiApiResponse 객체로 변환합니다.
            // .block()을 사용하여 비동기 호출을 동기 방식처럼 기다립니다.
            return this.webClient.post()
                    .uri("/api/fields/ai-suggest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiApiResponse.class)
                    .block();
        } catch (Exception e) {
            // API 호출에 실패할 경우(예: 서버 다운, 네트워크 오류 등)
            // 실제 운영 환경에서는 로깅 라이브러리를 사용해 에러를 기록하는 것이 좋습니다.
            System.err.println("Error calling AI API: " + e.getMessage());
            // 실패 시 빈 응답을 반환하여, 호출한 쪽에서 null 체크 후 기존 로직을 타도록 유도합니다.
            return new AiApiResponse();
        }
    }
}

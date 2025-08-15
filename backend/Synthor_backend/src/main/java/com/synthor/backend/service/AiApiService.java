package com.synthor.backend.service;

import com.synthor.backend.dto.AiApiAutoGenerateResponse;
import com.synthor.backend.dto.AiApiRequest;
import com.synthor.backend.dto.AiApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiApiService {

    private final WebClient webClient;
    private final String autoGenerateUrl;

    public AiApiService(WebClient.Builder webClientBuilder,
                        @Value("${ai.api.auto-generate.url}") String autoGenerateUrl) {
        this.webClient = webClientBuilder.build();
        this.autoGenerateUrl = autoGenerateUrl;
    }

    public AiApiResponse suggestFieldType(String prompt) {
        AiApiRequest requestBody = new AiApiRequest(prompt);

        try {
            return this.webClient.post()
                    .uri("https://synthor-ai.onrender.com/api/fields/ai-suggest") // 이 URL은 하드코딩된 상태로 둡니다.
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiApiResponse.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Error calling AI suggest API: " + e.getMessage());
            return new AiApiResponse();
        }
    }

    public AiApiAutoGenerateResponse autoGenerateFieldsFromPrompt(String prompt) {
        AiApiRequest requestBody = new AiApiRequest(prompt);
        try {
            return this.webClient.post()
                    .uri(autoGenerateUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiApiAutoGenerateResponse.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Error calling AI auto-generate API: " + e.getMessage());
            return null; // 실패 시 null을 반환하여 컨트롤러에서 에러 처리를 할 수 있도록 합니다.
        }
    }
}

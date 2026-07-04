package com.lietech.interviewanalyzer.analysis;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final WebClient aiServiceWebClient;

    public AiAnalysisController(WebClient aiServiceWebClient) {
        this.aiServiceWebClient = aiServiceWebClient;
    }

    @PostMapping("/answers/analyze")
    public Map<String, Object> analyzeAnswer(
            @Valid @RequestBody AnswerAnalysisRequest request) {

        return aiServiceWebClient.post()
                .uri("/api/answers/analyze")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public record AnswerAnalysisRequest(
            @NotBlank String question,
            @NotBlank String answer,
            String targetRole
    ) {}
}
package com.lietech.interviewanalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiServiceConfig {

    @Bean
    WebClient aiServiceWebClient(
            WebClient.Builder builder,
            @Value("${app.ai-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }
}

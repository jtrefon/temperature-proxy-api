package com.temperatureproxy.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Infrastructure configuration for external clients.
 */
@Slf4j
@Configuration
public class InfrastructureConfig {
    
    @Value("${weather.open-meteo.base-url:https://api.open-meteo.com}")
    private String openMeteoBaseUrl;
    
    @Bean
    public WebClient webClient() {
        log.info("Creating WebClient for Open-Meteo API at: {}", openMeteoBaseUrl);
        return WebClient.builder()
            .baseUrl(openMeteoBaseUrl)
            .build();
    }
}

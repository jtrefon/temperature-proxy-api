package com.temperatureproxy.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Infrastructure configuration for web client and health indicators.
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
    
    @Bean
    public CompositeHealthContributor compositeHealthContributor(
            Map<String, HealthContributor> contributors) {
        return CompositeHealthContributor.fromMap(contributors);
    }
}

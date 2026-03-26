package com.temperatureproxy.infrastructure.config;

import com.temperatureproxy.domain.repository.WeatherRepository;
import com.temperatureproxy.domain.service.WeatherDataSource;
import com.temperatureproxy.infrastructure.repository.CaffeineWeatherRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for weather data repository.
 */
@Slf4j
@Configuration
public class RepositoryConfig {
    
    @Value("${weather.cache.ttl:60s}")
    private Duration cacheTtl;
    
    @Value("${weather.cache.max-size:1000}")
    private int maxCacheSize;
    
    @Bean
    public WeatherRepository weatherRepository(WeatherDataSource dataSource) {
        log.info("Creating Caffeine cache repository with TTL={} and maxSize={}", 
            cacheTtl, maxCacheSize);
        return new CaffeineWeatherRepository(dataSource, cacheTtl, maxCacheSize);
    }
}

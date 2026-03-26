package com.temperatureproxy.infrastructure.client;

import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;
import com.temperatureproxy.domain.model.WeatherMetrics;
import com.temperatureproxy.domain.service.WeatherDataSource;
import com.temperatureproxy.infrastructure.client.dto.OpenMeteoResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter pattern implementation for Open-Meteo API.
 * Includes Circuit Breaker, Retry, and Time Limiter resilience patterns.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "Spring manages WebClient as a shared infrastructure dependency.")
public class OpenMeteoAdapter implements WeatherDataSource {
    
    private static final String SOURCE_NAME = "open-meteo";
    
    private final WebClient webClient;
    
    @Override
    @CircuitBreaker(name = "openMeteo", fallbackMethod = "fetchFallback")
    @Retry(name = "openMeteo")
    @TimeLimiter(name = "openMeteo")
    public CompletableFuture<WeatherData> fetch(Location location) {
        log.info("Fetching weather data from Open-Meteo for location: {}", location);
        
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/forecast")
                .queryParam("latitude", location.getLatitude())
                .queryParam("longitude", location.getLongitude())
                .queryParam("current", "temperature_2m,wind_speed_10m")
                .build())
            .retrieve()
            .bodyToMono(OpenMeteoResponse.class)
            .timeout(Duration.ofSeconds(1))
            .map(this::toDomain)
            .doOnNext(data -> log.info("Successfully fetched weather data: {}", data))
            .doOnError(error -> log.error("Failed to fetch weather data: {}", error.getMessage()))
            .toFuture();
    }
    
    /**
     * Fallback method when circuit breaker is open or call fails.
     */
    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "Resilience4j invokes fallback methods reflectively.")
    private CompletableFuture<WeatherData> fetchFallback(Location location, Exception ex) {
        log.warn("Fallback triggered for location: {} - Reason: {}", location, ex.getMessage());
        // Return cached data or throw specific exception
        throw new WeatherServiceException("Weather service temporarily unavailable", ex);
    }
    
    private WeatherData toDomain(OpenMeteoResponse response) {
        return WeatherData.create(
            Location.builder()
                .latitude(response.getLatitude())
                .longitude(response.getLongitude())
                .build(),
            WeatherMetrics.builder()
                .temperatureC(response.getCurrent().getTemperature2m())
                .windSpeedKmh(response.getCurrent().getWindSpeed10m())
                .build(),
            SOURCE_NAME
        );
    }
}

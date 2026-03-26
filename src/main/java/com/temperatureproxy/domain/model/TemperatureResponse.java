package com.temperatureproxy.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * API response DTO - immutable value object.
 */
@Value
@Builder
public final class TemperatureResponse {
    
    private final Location location;
    private final WeatherMetrics current;
    private final String source;
    private final Instant retrievedAt;
    
    public static TemperatureResponse from(WeatherData data) {
        return TemperatureResponse.builder()
            .location(data.getLocation())
            .current(data.getMetrics())
            .source(data.getSource())
            .retrievedAt(data.getRetrievedAt())
            .build();
    }
}

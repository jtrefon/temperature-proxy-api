package com.temperatureproxy.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Domain entity representing weather data from a specific source.
 * Immutable and thread-safe.
 */
@Value
@Builder
public final class WeatherData {
    
    private final Location location;
    private final WeatherMetrics metrics;
    private final String source;
    private final Instant retrievedAt;
    
    public static WeatherData create(Location location, 
                                      WeatherMetrics metrics, 
                                      String source) {
        return WeatherData.builder()
            .location(location)
            .metrics(metrics.normalize())
            .source(source)
            .retrievedAt(Instant.now())
            .build();
    }
}

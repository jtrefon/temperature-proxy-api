package com.temperatureproxy.domain.model;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable value object for weather measurements.
 * Normalizes values to standard precision.
 */
@Value
@Builder
public final class WeatherMetrics {
    
    private static final int TEMPERATURE_PRECISION = 1;
    private static final int WIND_SPEED_PRECISION = 1;
    
    private final double temperatureC;
    private final double windSpeedKmh;
    
    public WeatherMetrics normalize() {
        return WeatherMetrics.builder()
            .temperatureC(round(temperatureC, TEMPERATURE_PRECISION))
            .windSpeedKmh(round(windSpeedKmh, WIND_SPEED_PRECISION))
            .build();
    }
    
    private static double round(double value, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(value * factor) / factor;
    }
}

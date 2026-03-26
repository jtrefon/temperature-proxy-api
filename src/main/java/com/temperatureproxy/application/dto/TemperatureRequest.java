package com.temperatureproxy.application.dto;

import com.temperatureproxy.domain.model.Location;

import lombok.Builder;
import lombok.Value;

/**
 * DTO for temperature request query parameters.
 */
@Value
@Builder
public class TemperatureRequest {
    
    private final double latitude;
    private final double longitude;
    
    public static TemperatureRequest of(double latitude, double longitude) {
        return TemperatureRequest.builder()
            .latitude(latitude)
            .longitude(longitude)
            .build();
    }

    public Location toLocation() {
        return Location.of(latitude, longitude);
    }
}

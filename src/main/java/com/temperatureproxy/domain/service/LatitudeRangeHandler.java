package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;

/**
 * Validates latitude is within valid range (-90 to 90).
 */
public class LatitudeRangeHandler extends AbstractValidationHandler {
    
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    
    @Override
    public void validate(Location location) {
        double latitude = location.getLatitude();
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(
                String.format("Latitude must be between %.1f and %.1f, got: %.4f",
                    MIN_LATITUDE, MAX_LATITUDE, latitude));
        }
        next(location);
    }
}

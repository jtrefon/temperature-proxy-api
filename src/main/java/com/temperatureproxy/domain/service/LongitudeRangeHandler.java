package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;

/**
 * Validates longitude is within valid range (-180 to 180).
 */
public class LongitudeRangeHandler extends AbstractValidationHandler {
    
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    
    @Override
    public void validate(Location location) {
        double longitude = location.getLongitude();
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(
                String.format("Longitude must be between %.1f and %.1f, got: %.4f",
                    MIN_LONGITUDE, MAX_LONGITUDE, longitude));
        }
        next(location);
    }
}

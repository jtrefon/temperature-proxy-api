package com.temperatureproxy.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Immutable value object representing geographic coordinates.
 * Thread-safe and uses Builder pattern for construction.
 */
@Value
@Builder
public final class Location {
    
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final int PRECISION = 4;
    
    private final double latitude;
    private final double longitude;

    public static Location of(double latitude, double longitude) {
        return new Location(latitude, longitude);
    }
    
    @JsonCreator
    public Location(
            @JsonProperty("lat") double latitude,
            @JsonProperty("lon") double longitude) {
        this.latitude = roundToPrecision(latitude, PRECISION);
        this.longitude = roundToPrecision(longitude, PRECISION);
        validate();
    }
    
    private void validate() {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(
                String.format("Latitude must be between %f and %f, got: %f", 
                    MIN_LATITUDE, MAX_LATITUDE, latitude));
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(
                String.format("Longitude must be between %f and %f, got: %f", 
                    MIN_LONGITUDE, MAX_LONGITUDE, longitude));
        }
    }
    
    public String toCacheKey() {
        return String.format("%.4f:%.4f", latitude, longitude);
    }
    
    private static double roundToPrecision(double value, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(value * factor) / factor;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
               Double.compare(location.longitude, longitude) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
    
    @Override
    public String toString() {
        return String.format("Location{lat=%.4f, lon=%.4f}", latitude, longitude);
    }
}

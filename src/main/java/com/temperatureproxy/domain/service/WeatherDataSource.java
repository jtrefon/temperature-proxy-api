package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;

import java.util.concurrent.CompletableFuture;

/**
 * Core interface for weather data sources.
 * Used by Proxy pattern for caching layer.
 */
public interface WeatherDataSource {
    
    /**
     * Fetch weather data for a location.
     * @param location the location
     * @return CompletableFuture with weather data
     */
    CompletableFuture<WeatherData> fetch(Location location);
}

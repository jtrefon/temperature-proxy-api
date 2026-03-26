package com.temperatureproxy.domain.repository;

import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;

import java.util.Optional;

/**
 * Repository pattern for weather data access.
 * Abstracts the data source implementation.
 */
public interface WeatherRepository {
    
    /**
     * Find weather data by location.
     * @param location the location
     * @return Optional containing weather data if found
     */
    Optional<WeatherData> findByLocation(Location location);
    
    /**
     * Save weather data for a location.
     * @param location the location
     * @param data the weather data
     */
    void save(Location location, WeatherData data);
    
    /**
     * Evict cached data for a location.
     * @param location the location
     */
    void evict(Location location);
    
    /**
     * Get cache statistics.
     * @return cache statistics
     */
    CacheStatistics getStatistics();
    
    /**
     * Cache statistics record.
     */
    record CacheStatistics(long hitCount, long missCount, double hitRate) {}
}

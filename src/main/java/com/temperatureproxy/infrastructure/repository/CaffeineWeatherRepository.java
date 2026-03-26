package com.temperatureproxy.infrastructure.repository;

import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;
import com.temperatureproxy.domain.repository.WeatherRepository;
import com.temperatureproxy.domain.service.WeatherDataSource;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

/**
 * Repository implementation using Caffeine cache.
 * In-memory only - no external dependencies.
 */
@Slf4j
public class CaffeineWeatherRepository implements WeatherRepository {
    
    private final Cache<String, WeatherData> cache;
    private final WeatherDataSource dataSource;
    
    public CaffeineWeatherRepository(WeatherDataSource dataSource, 
                                      Duration ttl, 
                                      int maxSize) {
        this.dataSource = dataSource;
        this.cache = Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(ttl)
            .recordStats()
            .removalListener((key, value, cause) -> 
                log.debug("Cache entry removed: {} - {}", key, cause))
            .build();
    }
    
    @Override
    public Optional<WeatherData> findByLocation(Location location) {
        String cacheKey = location.toCacheKey();
        WeatherData data = cache.getIfPresent(cacheKey);
        
        if (data != null) {
            log.debug("Cache hit for location: {}", location);
            return Optional.of(data);
        }
        
        log.debug("Cache miss for location: {}", location);
        
        // Fetch from source and cache
        try {
            WeatherData fetched = dataSource.fetch(location).join();
            cache.put(cacheKey, fetched);
            return Optional.of(fetched);
        } catch (Exception e) {
            log.error("Failed to fetch weather data for location: {}", location, e);
            return Optional.empty();
        }
    }
    
    @Override
    public void save(Location location, WeatherData data) {
        cache.put(location.toCacheKey(), data);
        log.debug("Cached weather data for location: {}", location);
    }
    
    @Override
    public void evict(Location location) {
        cache.invalidate(location.toCacheKey());
        log.debug("Evicted cache for location: {}", location);
    }
    
    @Override
    public CacheStatistics getStatistics() {
        var stats = cache.stats();
        long hitCount = stats.hitCount();
        long missCount = stats.missCount();
        double hitRate = stats.hitRate();
        
        return new CacheStatistics(hitCount, missCount, hitRate);
    }
}

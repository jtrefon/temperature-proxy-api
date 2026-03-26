package com.temperatureproxy.application.service;

import com.temperatureproxy.application.dto.TemperatureRequest;
import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.TemperatureResponse;
import com.temperatureproxy.domain.model.WeatherData;
import com.temperatureproxy.domain.repository.WeatherRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Application service implementing the use case for getting current temperature.
 * Uses Repository pattern and includes metrics collection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCurrentTemperatureUseCase {
    
    private final WeatherRepository weatherRepository;
    private final MeterRegistry meterRegistry;
    
    /**
     * Execute the use case to get current temperature.
     * @param request the temperature request
     * @return temperature response
     */
    public TemperatureResponse execute(TemperatureRequest request) {
        Location location = Location.builder()
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            log.info("Executing GetCurrentTemperature use case for location: {}", location);
            
            WeatherData weatherData = weatherRepository.findByLocation(location)
                .orElseThrow(() -> new NoSuchElementException(
                    "Weather data not available for location: " + location));
            
            TemperatureResponse response = TemperatureResponse.from(weatherData);
            
            sample.stop(meterRegistry.timer("usecase.temperature.success"));
            meterRegistry.counter("temperature.requests.success").increment();
            
            log.info("Successfully retrieved temperature: {}", response);
            return response;
            
        } catch (Exception e) {
            sample.stop(meterRegistry.timer("usecase.temperature.failure"));
            meterRegistry.counter("temperature.requests.failure").increment();
            log.error("Failed to get temperature for location: {}", location, e);
            throw e;
        }
    }
}

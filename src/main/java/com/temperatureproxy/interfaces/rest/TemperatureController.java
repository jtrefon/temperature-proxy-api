package com.temperatureproxy.interfaces.rest;

import com.temperatureproxy.application.dto.TemperatureRequest;
import com.temperatureproxy.application.service.GetCurrentTemperatureUseCase;
import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.TemperatureResponse;
import com.temperatureproxy.domain.service.ValidationHandler;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for temperature endpoints.
 * Implements validation chain and proper HTTP semantics.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@Validated
@RequiredArgsConstructor
public class TemperatureController {
    
    private final GetCurrentTemperatureUseCase getCurrentTemperatureUseCase;
    private final ValidationHandler validationChain;
    private final MeterRegistry meterRegistry;
    
    @GetMapping("/temperature")
    public ResponseEntity<TemperatureResponse> getTemperature(
            @RequestParam @Min(-90) @Max(90) double lat,
            @RequestParam @Min(-180) @Max(180) double lon) {
        
        meterRegistry.counter("api.temperature.requests").increment();
        
        log.info("Received temperature request: lat={}, lon={}", lat, lon);
        
        // Build location and validate through chain
        Location location = Location.builder()
            .latitude(lat)
            .longitude(lon)
            .build();
        
        validationChain.validate(location);
        
        TemperatureRequest request = TemperatureRequest.of(lat, lon);
        TemperatureResponse response = getCurrentTemperatureUseCase.execute(request);
        
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(response);
    }
}

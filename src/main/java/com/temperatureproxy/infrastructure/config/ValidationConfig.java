package com.temperatureproxy.infrastructure.config;

import com.temperatureproxy.domain.service.LatitudeRangeHandler;
import com.temperatureproxy.domain.service.LongitudeRangeHandler;
import com.temperatureproxy.domain.service.ValidationHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for validation chain using Chain of Responsibility pattern.
 */
@Configuration
public class ValidationConfig {
    
    @Bean
    public ValidationHandler validationChain() {
        ValidationHandler latitudeHandler = new LatitudeRangeHandler();
        ValidationHandler longitudeHandler = new LongitudeRangeHandler();
        
        latitudeHandler.setNext(longitudeHandler);
        
        return latitudeHandler;
    }
}

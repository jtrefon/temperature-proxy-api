package com.temperatureproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
public class TemperatureProxyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TemperatureProxyApplication.class, args);
    }
}

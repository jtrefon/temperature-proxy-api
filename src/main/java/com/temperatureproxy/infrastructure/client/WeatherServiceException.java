package com.temperatureproxy.infrastructure.client;

/**
 * Exception for weather service failures.
 */
public class WeatherServiceException extends RuntimeException {
    
    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WeatherServiceException(String message) {
        super(message);
    }
}

package com.temperatureproxy.interfaces.exception;

import com.temperatureproxy.infrastructure.client.WeatherServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

/**
 * Global exception handler for REST controllers.
 * Provides consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException e) {
        log.warn("Data not found: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
    }
    
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(TimeoutException e) {
        log.error("Upstream timeout: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.GATEWAY_TIMEOUT)
            .body(createErrorResponse(HttpStatus.GATEWAY_TIMEOUT, "Upstream timeout"));
    }
    
    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<Map<String, Object>> handleWeatherServiceError(WeatherServiceException e) {
        log.error("Weather service error: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Weather service unavailable"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }
    
    private Map<String, Object> createErrorResponse(HttpStatus status, String message) {
        return Map.of(
            "timestamp", Instant.now().toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", message
        );
    }
}

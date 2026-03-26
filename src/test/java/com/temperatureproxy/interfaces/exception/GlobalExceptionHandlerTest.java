package com.temperatureproxy.interfaces.exception;

import com.temperatureproxy.infrastructure.client.WeatherServiceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should map illegal arguments to bad request")
    void shouldHandleIllegalArgument() {
        assertStatus(handler.handleValidationError(new IllegalArgumentException("bad input")),
            HttpStatus.BAD_REQUEST, "bad input");
    }

    @Test
    @DisplayName("Should map constraint violations to bad request")
    void shouldHandleConstraintViolation() {
        assertStatus(handler.handleConstraintViolation(new ConstraintViolationException("invalid", null)),
            HttpStatus.BAD_REQUEST, "invalid");
    }

    @Test
    @DisplayName("Should map missing data to not found")
    void shouldHandleNotFound() {
        assertStatus(handler.handleNotFound(new NoSuchElementException("missing")),
            HttpStatus.NOT_FOUND, "missing");
    }

    @Test
    @DisplayName("Should map upstream timeout to gateway timeout")
    void shouldHandleTimeout() {
        assertStatus(handler.handleTimeout(new TimeoutException("slow")),
            HttpStatus.GATEWAY_TIMEOUT, "Upstream timeout");
    }

    @Test
    @DisplayName("Should map weather service errors to service unavailable")
    void shouldHandleWeatherServiceError() {
        assertStatus(handler.handleWeatherServiceError(new WeatherServiceException("offline")),
            HttpStatus.SERVICE_UNAVAILABLE, "Weather service unavailable");
    }

    @Test
    @DisplayName("Should map unexpected errors to internal server error")
    void shouldHandleGenericError() {
        assertStatus(handler.handleGenericError(new RuntimeException("boom")),
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private void assertStatus(ResponseEntity<java.util.Map<String, Object>> response,
                              HttpStatus status,
                              String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).containsEntry("status", status.value());
        assertThat(response.getBody()).containsEntry("message", message);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsEntry("error", status.getReasonPhrase());
    }
}

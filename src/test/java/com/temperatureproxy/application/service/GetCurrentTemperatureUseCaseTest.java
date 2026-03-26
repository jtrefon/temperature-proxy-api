package com.temperatureproxy.application.service;

import com.temperatureproxy.application.dto.TemperatureRequest;
import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.TemperatureResponse;
import com.temperatureproxy.domain.model.WeatherData;
import com.temperatureproxy.domain.model.WeatherMetrics;
import com.temperatureproxy.domain.repository.WeatherRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("GetCurrentTemperatureUseCase tests")
class GetCurrentTemperatureUseCaseTest {

    @Test
    @DisplayName("Should return mapped temperature response and record success metrics")
    void shouldReturnMappedResponse() {
        WeatherRepository repository = Mockito.mock(WeatherRepository.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        GetCurrentTemperatureUseCase useCase = new GetCurrentTemperatureUseCase(repository, meterRegistry);

        WeatherData weatherData = WeatherData.builder()
            .location(Location.of(52.52, 13.41))
            .metrics(WeatherMetrics.builder().temperatureC(15.5).windSpeedKmh(12.3).build())
            .source("open-meteo")
            .retrievedAt(Instant.parse("2026-01-11T10:12:54Z"))
            .build();

        when(repository.findByLocation(Location.of(52.52, 13.41))).thenReturn(Optional.of(weatherData));

        TemperatureResponse response = useCase.execute(TemperatureRequest.of(52.52, 13.41));

        assertThat(response.getLocation()).isEqualTo(weatherData.getLocation());
        assertThat(response.getCurrent()).isEqualTo(weatherData.getMetrics());
        assertThat(meterRegistry.counter("temperature.requests.success").count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("usecase.temperature.success").timer()).isNotNull();
    }

    @Test
    @DisplayName("Should throw not found and record failure metrics when repository is empty")
    void shouldThrowWhenRepositoryReturnsEmpty() {
        WeatherRepository repository = Mockito.mock(WeatherRepository.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        GetCurrentTemperatureUseCase useCase = new GetCurrentTemperatureUseCase(repository, meterRegistry);

        when(repository.findByLocation(Location.of(52.52, 13.41))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(TemperatureRequest.of(52.52, 13.41)))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Weather data not available");

        assertThat(meterRegistry.counter("temperature.requests.failure").count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("usecase.temperature.failure").timer()).isNotNull();
    }
}

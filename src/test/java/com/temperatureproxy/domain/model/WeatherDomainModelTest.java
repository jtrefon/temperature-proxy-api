package com.temperatureproxy.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Weather domain model tests")
class WeatherDomainModelTest {

    @Test
    @DisplayName("Should normalize weather metrics to one decimal place")
    void shouldNormalizeWeatherMetrics() {
        WeatherMetrics metrics = WeatherMetrics.builder()
            .temperatureC(15.54)
            .windSpeedKmh(12.26)
            .build();

        WeatherMetrics normalized = metrics.normalize();

        assertThat(normalized.getTemperatureC()).isEqualTo(15.5);
        assertThat(normalized.getWindSpeedKmh()).isEqualTo(12.3);
    }

    @Test
    @DisplayName("Should create normalized weather data with source and timestamp")
    void shouldCreateWeatherData() {
        Location location = Location.of(52.52, 13.41);
        WeatherMetrics metrics = WeatherMetrics.builder()
            .temperatureC(15.54)
            .windSpeedKmh(12.26)
            .build();

        WeatherData data = WeatherData.create(location, metrics, "open-meteo");

        assertThat(data.getLocation()).isEqualTo(location);
        assertThat(data.getMetrics().getTemperatureC()).isEqualTo(15.5);
        assertThat(data.getMetrics().getWindSpeedKmh()).isEqualTo(12.3);
        assertThat(data.getSource()).isEqualTo("open-meteo");
        assertThat(data.getRetrievedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Should map weather data to API response")
    void shouldMapTemperatureResponseFromWeatherData() {
        WeatherData data = WeatherData.builder()
            .location(Location.of(52.52, 13.41))
            .metrics(WeatherMetrics.builder()
                .temperatureC(15.5)
                .windSpeedKmh(12.3)
                .build())
            .source("open-meteo")
            .retrievedAt(Instant.parse("2026-01-11T10:12:54Z"))
            .build();

        TemperatureResponse response = TemperatureResponse.from(data);

        assertThat(response.getLocation()).isEqualTo(data.getLocation());
        assertThat(response.getCurrent()).isEqualTo(data.getMetrics());
        assertThat(response.getSource()).isEqualTo("open-meteo");
        assertThat(response.getRetrievedAt()).isEqualTo(Instant.parse("2026-01-11T10:12:54Z"));
    }
}

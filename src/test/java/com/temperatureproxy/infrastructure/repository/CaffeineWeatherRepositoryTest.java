package com.temperatureproxy.infrastructure.repository;

import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;
import com.temperatureproxy.domain.model.WeatherMetrics;
import com.temperatureproxy.domain.repository.WeatherRepository;
import com.temperatureproxy.domain.service.WeatherDataSource;
import com.temperatureproxy.infrastructure.client.WeatherServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CaffeineWeatherRepository tests")
class CaffeineWeatherRepositoryTest {

    @Test
    @DisplayName("Should cache fetched values and expose cache statistics")
    void shouldCacheFetchedValues() {
        AtomicInteger fetchCount = new AtomicInteger();
        WeatherDataSource dataSource = location -> {
            fetchCount.incrementAndGet();
            return CompletableFuture.completedFuture(weatherData(location));
        };
        WeatherRepository repository = new CaffeineWeatherRepository(dataSource, Duration.ofMinutes(1), 100);
        Location location = Location.of(52.52, 13.41);

        Optional<WeatherData> firstFetch = repository.findByLocation(location);
        Optional<WeatherData> secondFetch = repository.findByLocation(location);

        assertThat(firstFetch).isPresent();
        assertThat(secondFetch).isPresent();
        assertThat(fetchCount).hasValue(1);
        assertThat(repository.getStatistics().hitCount()).isEqualTo(1);
        assertThat(repository.getStatistics().missCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should allow manual cache save and eviction")
    void shouldSaveAndEvict() {
        AtomicInteger fetchCount = new AtomicInteger();
        WeatherDataSource dataSource = location -> {
            fetchCount.incrementAndGet();
            return CompletableFuture.completedFuture(weatherData(location));
        };
        WeatherRepository repository = new CaffeineWeatherRepository(dataSource, Duration.ofMinutes(1), 100);
        Location location = Location.of(52.52, 13.41);

        repository.save(location, weatherData(location));

        assertThat(repository.findByLocation(location)).isPresent();
        assertThat(fetchCount).hasValue(0);

        repository.evict(location);
        assertThat(repository.findByLocation(location)).isPresent();

        assertThat(fetchCount).hasValue(1);
    }

    @Test
    @DisplayName("Should rethrow upstream runtime failures as weather service exceptions")
    void shouldRethrowFailures() {
        WeatherDataSource dataSource = location -> CompletableFuture.failedFuture(
            new WeatherServiceException("Upstream unavailable"));
        WeatherRepository repository = new CaffeineWeatherRepository(dataSource, Duration.ofMinutes(1), 100);

        assertThatThrownBy(() -> repository.findByLocation(Location.of(52.52, 13.41)))
            .isInstanceOf(WeatherServiceException.class)
            .hasMessageContaining("Upstream unavailable");
    }

    private WeatherData weatherData(Location location) {
        return WeatherData.create(
            location,
            WeatherMetrics.builder()
                .temperatureC(15.5)
                .windSpeedKmh(12.3)
                .build(),
            "open-meteo"
        );
    }
}

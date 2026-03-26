package com.temperatureproxy.interfaces.rest;

import com.temperatureproxy.application.service.GetCurrentTemperatureUseCase;
import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.TemperatureResponse;
import com.temperatureproxy.domain.model.WeatherMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("TemperatureController tests")
class TemperatureControllerTest {

    @Test
    @DisplayName("Should delegate to use case, increment metrics, and set cache headers")
    void shouldDelegateAndBuildResponse() {
        GetCurrentTemperatureUseCase useCase = Mockito.mock(GetCurrentTemperatureUseCase.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        TemperatureController controller = new TemperatureController(useCase, meterRegistry);
        TemperatureResponse expectedResponse = TemperatureResponse.builder()
            .location(Location.of(52.52, 13.41))
            .current(WeatherMetrics.builder().temperatureC(15.5).windSpeedKmh(12.3).build())
            .source("open-meteo")
            .retrievedAt(Instant.parse("2026-01-11T10:12:54Z"))
            .build();

        when(useCase.execute(Mockito.any())).thenReturn(expectedResponse);

        var response = controller.getTemperature(52.52, 13.41);
        ArgumentCaptor<com.temperatureproxy.application.dto.TemperatureRequest> requestCaptor =
            ArgumentCaptor.forClass(com.temperatureproxy.application.dto.TemperatureRequest.class);

        verify(useCase).execute(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getLatitude()).isEqualTo(52.52);
        assertThat(requestCaptor.getValue().getLongitude()).isEqualTo(13.41);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        assertThat(response.getHeaders().getCacheControl()).isEqualTo("max-age=60");
        assertThat(meterRegistry.counter("api.temperature.requests").count()).isEqualTo(1.0);
    }
}

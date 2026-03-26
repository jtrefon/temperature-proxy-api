package com.temperatureproxy.infrastructure.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.temperatureproxy.domain.model.Location;
import com.temperatureproxy.domain.model.WeatherData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OpenMeteoAdapter tests")
class OpenMeteoAdapterTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(9091);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Should map Open-Meteo response into domain weather data")
    void shouldFetchAndMapResponse() {
        wireMockServer.stubFor(get(urlPathEqualTo("/v1/forecast"))
            .withQueryParam("latitude", equalTo("52.52"))
            .withQueryParam("longitude", equalTo("13.41"))
            .withQueryParam("current", equalTo("temperature_2m,wind_speed_10m"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "latitude": 52.52,
                      "longitude": 13.41,
                      "current": {
                        "temperature_2m": 15.54,
                        "wind_speed_10m": 12.26,
                        "time": "2026-01-11T10:00:00"
                      }
                    }
                    """)));

        OpenMeteoAdapter adapter = new OpenMeteoAdapter(
            WebClient.builder().baseUrl("http://localhost:9091").build());

        WeatherData data = adapter.fetch(Location.of(52.52, 13.41)).join();

        assertThat(data.getLocation()).isEqualTo(Location.of(52.52, 13.41));
        assertThat(data.getMetrics().getTemperatureC()).isEqualTo(15.5);
        assertThat(data.getMetrics().getWindSpeedKmh()).isEqualTo(12.3);
        assertThat(data.getSource()).isEqualTo("open-meteo");
        assertThat(data.getRetrievedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should complete exceptionally when upstream responds with an error")
    void shouldFailWhenUpstreamReturnsError() {
        wireMockServer.stubFor(get(urlPathEqualTo("/v1/forecast"))
            .willReturn(aResponse().withStatus(500)));

        OpenMeteoAdapter adapter = new OpenMeteoAdapter(
            WebClient.builder().baseUrl("http://localhost:9091").build());

        assertThatThrownBy(() -> adapter.fetch(Location.of(52.52, 13.41)).join())
            .isInstanceOf(CompletionException.class);
    }

    @Test
    @DisplayName("Should expose a fallback that raises service-unavailable semantics")
    void shouldFailThroughFallbackMethod() throws Exception {
        OpenMeteoAdapter adapter = new OpenMeteoAdapter(
            WebClient.builder().baseUrl("http://localhost:9091").build());

        Method fallback = OpenMeteoAdapter.class.getDeclaredMethod(
            "fetchFallback", Location.class, Exception.class);
        fallback.setAccessible(true);

        assertThatThrownBy(() -> invokeFallback(fallback, adapter))
            .isInstanceOf(WeatherServiceException.class)
            .hasMessage("Weather service temporarily unavailable")
            .hasCauseInstanceOf(IllegalStateException.class);
    }

    private static Object invokeFallback(Method fallback, OpenMeteoAdapter adapter) throws Throwable {
        try {
            return fallback.invoke(adapter, Location.of(52.52, 13.41), new IllegalStateException("boom"));
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}

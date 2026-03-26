package com.temperatureproxy.interfaces.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Temperature API Integration Tests")
class TemperatureControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private WireMockServer wireMockServer;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.open-meteo.base-url", () -> "http://localhost:9090");
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Should return temperature for valid coordinates")
    void shouldReturnTemperatureForValidCoordinates() {
        // Given
        String openMeteoResponse = """
            {
                "latitude": 52.52,
                "longitude": 13.41,
                "current": {
                    "temperature_2m": 15.5,
                    "wind_speed_10m": 12.3,
                    "time": "2026-01-11T10:00:00"
                }
            }
            """;

        wireMockServer.stubFor(get(urlPathEqualTo("/v1/forecast"))
            .withQueryParam("latitude", equalTo("52.52"))
            .withQueryParam("longitude", equalTo("13.41"))
            .withQueryParam("current", equalTo("temperature_2m,wind_speed_10m"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(openMeteoResponse)));

        // When & Then
        webTestClient.get()
            .uri("/v1/temperature?lat=52.52&lon=13.41")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Cache-Control", "max-age=60")
            .expectBody()
            .jsonPath("$.location.lat").isEqualTo(52.52)
            .jsonPath("$.location.lon").isEqualTo(13.41)
            .jsonPath("$.current.temperatureC").isEqualTo(15.5)
            .jsonPath("$.current.windSpeedKmh").isEqualTo(12.3)
            .jsonPath("$.source").isEqualTo("open-meteo")
            .jsonPath("$.retrievedAt").exists();
    }

    @Test
    @DisplayName("Should return 400 for invalid latitude")
    void shouldReturn400ForInvalidLatitude() {
        webTestClient.get()
            .uri("/v1/temperature?lat=91.0&lon=13.41")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return 400 for invalid longitude")
    void shouldReturn400ForInvalidLongitude() {
        webTestClient.get()
            .uri("/v1/temperature?lat=52.52&lon=181.0")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle upstream timeout")
    void shouldHandleUpstreamTimeout() {
        wireMockServer.stubFor(get(urlPathEqualTo("/v1/forecast"))
            .willReturn(aResponse()
                .withFixedDelay(5000)));

        webTestClient.get()
            .uri("/v1/temperature?lat=52.52&lon=13.41")
            .exchange()
            .expectStatus().isEqualTo(503);
    }
}

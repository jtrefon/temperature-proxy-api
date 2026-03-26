package com.temperatureproxy.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

/**
 * DTO for Open-Meteo API response.
 * Maps JSON structure from Open-Meteo Forecast API.
 */
@Data
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = "Jackson populates this mutable transport DTO during deserialization.")
public class OpenMeteoResponse {
    
    private double latitude;
    private double longitude;
    
    @JsonProperty("current")
    private CurrentWeather current;
    
    @Data
    public static class CurrentWeather {
        
        @JsonProperty("temperature_2m")
        private double temperature2m;
        
        @JsonProperty("wind_speed_10m")
        private double windSpeed10m;
        
        @JsonProperty("time")
        private String time;
    }
}

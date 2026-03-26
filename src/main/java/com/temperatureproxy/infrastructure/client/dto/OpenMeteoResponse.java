package com.temperatureproxy.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for Open-Meteo API response.
 * Maps JSON structure from Open-Meteo Forecast API.
 */
@Data
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

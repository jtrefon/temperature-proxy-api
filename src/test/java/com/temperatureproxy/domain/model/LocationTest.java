package com.temperatureproxy.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Location Domain Model Tests")
class LocationTest {
    
    @Test
    @DisplayName("Should create valid location with rounding")
    void shouldCreateValidLocation() {
        Location location = Location.builder()
            .latitude(52.520006)
            .longitude(13.404954)
            .build();
        
        assertThat(location.getLatitude()).isEqualTo(52.5200);
        assertThat(location.getLongitude()).isEqualTo(13.4050);
    }
    
    @Test
    @DisplayName("Should reject latitude below -90")
    void shouldRejectLatitudeBelowMin() {
        assertThatThrownBy(() -> Location.builder()
            .latitude(-91.0)
            .longitude(0.0)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude must be between");
    }
    
    @Test
    @DisplayName("Should reject latitude above 90")
    void shouldRejectLatitudeAboveMax() {
        assertThatThrownBy(() -> Location.builder()
            .latitude(91.0)
            .longitude(0.0)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude must be between");
    }
    
    @Test
    @DisplayName("Should reject longitude below -180")
    void shouldRejectLongitudeBelowMin() {
        assertThatThrownBy(() -> Location.builder()
            .latitude(0.0)
            .longitude(-181.0)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Longitude must be between");
    }
    
    @Test
    @DisplayName("Should reject longitude above 180")
    void shouldRejectLongitudeAboveMax() {
        assertThatThrownBy(() -> Location.builder()
            .latitude(0.0)
            .longitude(181.0)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Longitude must be between");
    }
    
    @Test
    @DisplayName("Should generate consistent cache key")
    void shouldGenerateConsistentCacheKey() {
        Location location = Location.builder()
            .latitude(52.52)
            .longitude(13.41)
            .build();
        
        String cacheKey = location.toCacheKey();
        assertThat(cacheKey).isEqualTo("52.5200:13.4100");
    }
    
    @Test
    @DisplayName("Should be equal with same coordinates")
    void shouldBeEqualWithSameCoordinates() {
        Location location1 = Location.builder()
            .latitude(52.52)
            .longitude(13.41)
            .build();
        
        Location location2 = Location.builder()
            .latitude(52.52)
            .longitude(13.41)
            .build();
        
        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }
}

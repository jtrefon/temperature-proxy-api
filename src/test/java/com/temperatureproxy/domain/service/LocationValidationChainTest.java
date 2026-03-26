package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Location Validation Chain Tests")
class LocationValidationChainTest {
    
    private ValidationHandler chain;
    
    @BeforeEach
    void setUp() {
        chain = new LatitudeRangeHandler();
        ValidationHandler longitudeHandler = new LongitudeRangeHandler();
        chain.setNext(longitudeHandler);
    }
    
    @Test
    @DisplayName("Should accept valid coordinates")
    void shouldAcceptValidCoordinates() {
        Location valid = Location.builder()
            .latitude(52.52)
            .longitude(13.41)
            .build();
        
        assertDoesNotThrow(() -> chain.validate(valid));
    }
    
    @Test
    @DisplayName("Should reject out-of-range latitude")
    void shouldRejectOutOfRangeLatitude() {
        Location invalid = Location.builder()
            .latitude(91.0)
            .longitude(0.0)
            .build();
        
        assertThatThrownBy(() -> chain.validate(invalid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude must be between");
    }
    
    @Test
    @DisplayName("Should reject out-of-range longitude")
    void shouldRejectOutOfRangeLongitude() {
        Location invalid = Location.builder()
            .latitude(0.0)
            .longitude(181.0)
            .build();
        
        assertThatThrownBy(() -> chain.validate(invalid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Longitude must be between");
    }
    
    @Test
    @DisplayName("Should validate latitude before longitude")
    void shouldValidateLatitudeBeforeLongitude() {
        Location invalidBoth = Location.builder()
            .latitude(91.0)
            .longitude(181.0)
            .build();
        
        assertThatThrownBy(() -> chain.validate(invalidBoth))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude");
    }
}

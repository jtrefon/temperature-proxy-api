package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;

/**
 * Chain of Responsibility pattern for validation.
 * Allows building a pipeline of validation handlers.
 */
public interface ValidationHandler {
    
    /**
     * Set the next handler in the chain.
     * @param next the next validation handler
     */
    void setNext(ValidationHandler next);
    
    /**
     * Validate the location. If validation passes, calls next handler.
     * @param location the location to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validate(Location location);
}

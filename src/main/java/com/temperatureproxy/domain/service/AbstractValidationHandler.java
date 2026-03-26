package com.temperatureproxy.domain.service;

import com.temperatureproxy.domain.model.Location;

/**
 * Abstract base class for validation handlers.
 * Provides common chaining logic.
 */
public abstract class AbstractValidationHandler implements ValidationHandler {
    
    private ValidationHandler next;
    
    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }
    
    protected void next(Location location) {
        if (next != null) {
            next.validate(location);
        }
    }
}

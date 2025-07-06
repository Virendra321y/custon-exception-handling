package com.properException.handle.exceptions;

import java.util.Map;

public class ResourceValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ResourceValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}


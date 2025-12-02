package com.example.crmbackend.exception;

/**
 * Exception thrown when too many requests are made within a short time period
 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}


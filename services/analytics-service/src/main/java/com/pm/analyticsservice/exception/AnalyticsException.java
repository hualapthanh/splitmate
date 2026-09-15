package com.pm.analyticsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnalyticsException extends RuntimeException {
    public AnalyticsException(String message) {
        super(message);
    }
}

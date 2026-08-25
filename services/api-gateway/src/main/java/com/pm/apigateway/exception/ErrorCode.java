package com.pm.apigateway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    GW_001("GW-001", HttpStatus.NOT_FOUND, "Route Not Found"),
    GW_002("GW-002", HttpStatus.UNAUTHORIZED, "Invalid JWT Access Token"),
    GW_003("GW-003", HttpStatus.UNAUTHORIZED, "Unauthorized Request"),
    GW_004("GW-004", HttpStatus.FORBIDDEN, "Access Denied"),
    GW_005("GW-005", HttpStatus.TOO_MANY_REQUESTS, "Rate Limit Exceeded"),
    GW_006("GW-006", HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable"),
    INTERNAL_SERVER_ERROR("SYS-500", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Gateway Error");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

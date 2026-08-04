package com.pm.userservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_001("USER-001", HttpStatus.BAD_REQUEST, "Validation Failed"),
    USER_002("USER-002", HttpStatus.NOT_FOUND, "User Not Found"),
    USER_003("USER-003", HttpStatus.CONFLICT, "User Email Already Exists"),
    RESOURCE_NOT_FOUND("RESOURCE-404", HttpStatus.NOT_FOUND, "Resource Not Found"),
    INTERNAL_SERVER_ERROR("SYS-500", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

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

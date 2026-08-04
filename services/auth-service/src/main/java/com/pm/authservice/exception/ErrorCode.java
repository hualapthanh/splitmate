package com.pm.authservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    AUTH_001("AUTH-001", HttpStatus.BAD_REQUEST, "Validation Failed"),
    AUTH_002("AUTH-002", HttpStatus.UNAUTHORIZED, "Invalid Credentials"),
    AUTH_003("AUTH-003", HttpStatus.LOCKED, "Account Locked"),
    AUTH_004("AUTH-004", HttpStatus.CONFLICT, "Email Already Exists"),
    AUTH_005("AUTH-005", HttpStatus.UNAUTHORIZED, "Invalid Access Token"),
    AUTH_006("AUTH-006", HttpStatus.UNAUTHORIZED, "Refresh Token Expired"),
    AUTH_007("AUTH-007", HttpStatus.UNAUTHORIZED, "Refresh Token Revoked"),
    AUTH_008("AUTH-008", HttpStatus.UNAUTHORIZED, "Unauthorized"),
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

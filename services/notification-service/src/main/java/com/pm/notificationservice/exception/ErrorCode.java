package com.pm.notificationservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOTIF_001("NOTIF-001", HttpStatus.NOT_FOUND, "Notification Not Found"),
    NOTIF_002("NOTIF-002", HttpStatus.BAD_REQUEST, "Cannot send payment reminder to yourself"),
    VALIDATION_FAILED("VALIDATION-400", HttpStatus.BAD_REQUEST, "Validation Failed"),
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

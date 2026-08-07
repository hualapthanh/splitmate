package com.pm.userservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_001("USER-001", HttpStatus.NOT_FOUND, "Profile Not Found"),
    USER_002("USER-002", HttpStatus.BAD_REQUEST, "Invalid Avatar URL"),
    USER_004("USER-004", HttpStatus.BAD_REQUEST, "Invalid Timezone"),
    USER_005("USER-005", HttpStatus.BAD_REQUEST, "Invalid Locale"),
    USER_006("USER-006", HttpStatus.UNAUTHORIZED, "Unauthorized Access"),
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

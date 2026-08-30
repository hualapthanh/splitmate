package com.pm.balanceservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAL_001("BAL-001", HttpStatus.NOT_FOUND, "Balance Record Not Found"),
    BAL_002("BAL-002", HttpStatus.NOT_FOUND, "Settlement Not Found"),
    BAL_003("BAL-003", HttpStatus.BAD_REQUEST, "Invalid Settlement Amount"),
    BAL_004("BAL-004", HttpStatus.BAD_REQUEST, "Cannot create settlement where payer and payee are the same user"),
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

package com.pm.expenseservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EXP_001("EXP-001", HttpStatus.NOT_FOUND, "Expense Not Found"),
    EXP_002("EXP-002", HttpStatus.BAD_REQUEST, "Sum of amount_paid by payers must equal total expense amount"),
    EXP_003("EXP-003", HttpStatus.BAD_REQUEST, "Sum of split amounts must equal total expense amount"),
    EXP_004("EXP-004", HttpStatus.BAD_REQUEST, "Sum of split percentages must equal exactly 100.00%"),
    EXP_005("EXP-005", HttpStatus.NOT_FOUND, "Category Not Found"),
    EXP_006("EXP-006", HttpStatus.FORBIDDEN, "Access Denied. Only creator can modify/delete expense"),
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

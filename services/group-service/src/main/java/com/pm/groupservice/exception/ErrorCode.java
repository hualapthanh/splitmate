package com.pm.groupservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    GRP_001("GRP-001", HttpStatus.NOT_FOUND, "Group Not Found"),
    GRP_002("GRP-002", HttpStatus.BAD_REQUEST, "Invalid or Expired Invite Code"),
    GRP_003("GRP-003", HttpStatus.FORBIDDEN, "Access Denied. Group Admin permission required"),
    GRP_004("GRP-004", HttpStatus.CONFLICT, "User is already an active member of this group"),
    GRP_005("GRP-005", HttpStatus.BAD_REQUEST, "Cannot remove the owner of the group"),
    GRP_006("GRP-006", HttpStatus.BAD_REQUEST, "Group must have at least one active Admin"),
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

package com.pm.userservice.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message); // placeholder, as user-service doesn't own auth codes
    }

    public UnauthorizedException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }
}

package com.pm.authservice.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCode.AUTH_008, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public UnauthorizedException() {
        super(ErrorCode.AUTH_008);
    }
}

package com.pm.authservice.exception;

public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}

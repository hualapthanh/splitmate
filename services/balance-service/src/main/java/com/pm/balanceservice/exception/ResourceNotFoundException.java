package com.pm.balanceservice.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.BAL_001, message);
    }

    public ResourceNotFoundException() {
        super(ErrorCode.BAL_001);
    }
}

package com.pm.expenseservice.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.EXP_001, message);
    }

    public ResourceNotFoundException() {
        super(ErrorCode.EXP_001);
    }
}

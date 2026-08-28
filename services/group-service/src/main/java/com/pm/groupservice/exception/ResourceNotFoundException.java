package com.pm.groupservice.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.GRP_001, message);
    }

    public ResourceNotFoundException() {
        super(ErrorCode.GRP_001);
    }
}

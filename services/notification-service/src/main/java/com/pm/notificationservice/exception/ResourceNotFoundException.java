package com.pm.notificationservice.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.NOTIF_001, message);
    }

    public ResourceNotFoundException() {
        super(ErrorCode.NOTIF_001);
    }
}

package com.ims.common.exception;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(message, "CONFLICT");
    }

    public ConflictException(String message, String errorCode) {
        super(message, errorCode);
    }
}

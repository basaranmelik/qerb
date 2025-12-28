package com.badsector.qerb.shared.domain.exception;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
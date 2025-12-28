package com.badsector.qerb.shared.domain.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
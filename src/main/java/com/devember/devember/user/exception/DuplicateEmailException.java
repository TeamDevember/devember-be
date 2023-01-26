package com.devember.devember.user.exception;

import com.devember.devember.config.error.exception.BusinessException;
import com.devember.devember.config.error.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String message) {
        super(message, ErrorCode.DUPLICATE_EMAIL);
    }
}

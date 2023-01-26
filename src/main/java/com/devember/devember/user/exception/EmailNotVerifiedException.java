package com.devember.devember.user.exception;

import com.devember.devember.config.error.exception.BusinessException;
import com.devember.devember.config.error.exception.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException(String message) {
        super(message, ErrorCode.EMAIL_NOT_VERIFIED);
    }
}

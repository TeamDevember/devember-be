package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException(String message) {
        super(message, ErrorCode.EMAIL_NOT_VERIFIED);
    }
}

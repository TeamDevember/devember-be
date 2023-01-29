package com.gridians.girdians.domain.user.exception;

import com.gridians.girdians.global.error.exception.BusinessException;
import com.gridians.girdians.global.error.exception.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException(String message) {
        super(message, ErrorCode.EMAIL_NOT_VERIFIED);
    }
}

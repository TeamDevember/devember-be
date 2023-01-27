package com.gridianz.girdianz.domain.user.exception;

import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException(String message) {
        super(message, ErrorCode.EMAIL_NOT_VERIFIED);
    }
}

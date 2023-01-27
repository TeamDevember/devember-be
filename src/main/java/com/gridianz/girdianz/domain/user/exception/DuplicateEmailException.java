package com.gridianz.girdianz.domain.user.exception;

import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String message) {
        super(message, ErrorCode.DUPLICATE_EMAIL);
    }
}

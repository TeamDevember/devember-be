package com.gridians.girdians.domain.user.exception;

import com.gridians.girdians.global.error.exception.BusinessException;
import com.gridians.girdians.global.error.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String message) {
        super(message, ErrorCode.DUPLICATE_EMAIL);
    }
}

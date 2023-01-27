package com.gridianz.girdianz.domain.user.exception;

import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.ErrorCode;

public class UserDeleteException extends BusinessException {
    public UserDeleteException(String message) {
        super(message, ErrorCode.DELETE_USER_ACCESS);

    }
}

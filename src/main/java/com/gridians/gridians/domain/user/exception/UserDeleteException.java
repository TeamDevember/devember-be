package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class UserDeleteException extends BusinessException {
    public UserDeleteException(String message) {
        super(message, ErrorCode.DELETE_USER_ACCESS);

    }
}

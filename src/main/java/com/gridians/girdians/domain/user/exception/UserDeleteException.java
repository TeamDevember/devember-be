package com.gridians.girdians.domain.user.exception;

import com.gridians.girdians.global.error.exception.BusinessException;
import com.gridians.girdians.global.error.exception.ErrorCode;

public class UserDeleteException extends BusinessException {
    public UserDeleteException(String message) {
        super(message, ErrorCode.DELETE_USER_ACCESS);

    }
}

package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class PasswordNotMatchException extends BusinessException {
    public PasswordNotMatchException(String message){
        super(message, ErrorCode.WRONG_USER_PASSWORD);
    }
}

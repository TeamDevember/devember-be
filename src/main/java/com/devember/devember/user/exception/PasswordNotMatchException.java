package com.devember.devember.user.exception;

import com.devember.devember.config.error.exception.BusinessException;
import com.devember.devember.config.error.exception.ErrorCode;

public class PasswordNotMatchException extends BusinessException {
    public PasswordNotMatchException(String message){
        super(message, ErrorCode.PASSWORD_NOT_MATCH);
    }
}

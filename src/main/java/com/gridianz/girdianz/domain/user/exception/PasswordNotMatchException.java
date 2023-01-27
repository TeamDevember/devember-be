package com.gridianz.girdianz.domain.user.exception;

import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.ErrorCode;

public class PasswordNotMatchException extends BusinessException {
    public PasswordNotMatchException(String message){
        super(message, ErrorCode.PASSWORD_NOT_MATCH);
    }
}

package com.gridians.girdians.domain.user.exception;

import com.gridians.girdians.global.error.exception.BusinessException;
import com.gridians.girdians.global.error.exception.ErrorCode;

public class PasswordNotMatchException extends BusinessException {
    public PasswordNotMatchException(String message){
        super(message, ErrorCode.PASSWORD_NOT_MATCH);
    }
}

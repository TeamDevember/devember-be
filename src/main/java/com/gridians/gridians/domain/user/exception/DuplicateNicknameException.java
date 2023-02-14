package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class DuplicateNicknameException extends BusinessException {
    public DuplicateNicknameException(String message) {
        super(message, ErrorCode.DUPLICATED_NICKNAME);
    }
}

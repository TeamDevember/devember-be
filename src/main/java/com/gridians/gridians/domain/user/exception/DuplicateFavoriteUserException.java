package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class DuplicateFavoriteUserException extends BusinessException {
    public DuplicateFavoriteUserException(String message) {
        super(message, ErrorCode.DUPLICATED_FAVORITE_USER);
    }
}

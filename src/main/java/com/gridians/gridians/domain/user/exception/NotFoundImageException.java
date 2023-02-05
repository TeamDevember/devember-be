package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class NotFoundImageException extends BusinessException {

    public NotFoundImageException(String message) {
        super(message, ErrorCode.IMAGE_NOT_FOUND);
    }
}

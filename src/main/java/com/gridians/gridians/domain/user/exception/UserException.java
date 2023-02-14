package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.domain.user.type.UserErrorCode;
import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserException extends BusinessException {

	public UserException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}

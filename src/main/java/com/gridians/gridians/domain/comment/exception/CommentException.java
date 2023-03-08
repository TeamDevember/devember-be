package com.gridians.gridians.domain.comment.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;

public class CommentException extends BusinessException {

	public CommentException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}

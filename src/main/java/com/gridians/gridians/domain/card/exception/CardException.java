package com.gridians.gridians.domain.card.exception;

import com.gridians.gridians.global.error.exception.BusinessException;
import com.gridians.gridians.global.error.exception.ErrorCode;


public class CardException extends BusinessException {

	public CardException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}

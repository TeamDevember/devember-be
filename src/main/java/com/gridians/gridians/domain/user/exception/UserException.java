package com.gridians.gridians.domain.user.exception;

import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserException extends RuntimeException{
	private UserErrorCode userErrorCode;

	public UserException(UserErrorCode userErrorCode) {
		super(userErrorCode.getDescription());
		this.userErrorCode = userErrorCode;
		log.info("user not found exception");
	}
}

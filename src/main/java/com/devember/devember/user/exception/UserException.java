package com.devember.devember.user.exception;

import com.devember.devember.user.type.UserErrorCode;


public class UserException extends RuntimeException{
	private UserErrorCode userErrorCode;

	public UserException(UserErrorCode userErrorCode) {
		super(userErrorCode.getDescription());
		this.userErrorCode = userErrorCode;
	}
}

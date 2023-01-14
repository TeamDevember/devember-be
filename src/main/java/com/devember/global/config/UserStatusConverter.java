package com.devember.global.config;


import com.devember.devember.user.type.UserStatus;
import org.springframework.core.convert.converter.Converter;

public class UserStatusConverter implements Converter<String, UserStatus> {

	@Override
	public UserStatus convert(String userStatus) {
		return UserStatus.valueOf(userStatus);
	}
}



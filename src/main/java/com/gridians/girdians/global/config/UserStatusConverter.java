package com.gridians.girdians.global.config;


import com.gridians.girdians.domain.user.type.UserStatus;
import org.springframework.core.convert.converter.Converter;

public class UserStatusConverter implements Converter<String, UserStatus> {

	@Override
	public UserStatus convert(String userStatus) {
		return UserStatus.valueOf(userStatus);
	}
}



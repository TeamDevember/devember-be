package com.gridianz.girdianz.domain.user.type;

public enum UserStatus {
	ACTIVE("active"), UNACTIVE("unactive"), DELETED("deleted"), BLOCKED("block");


	private String value;

	UserStatus(String value) {
		this.value = value;
	}
}

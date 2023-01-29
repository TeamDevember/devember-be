package com.gridians.gridians.domain.user.type;

public enum UserStatus {
	ACTIVE("active"), UNACTIVE("unactive"), BLOCKED("block"), DELETED("deleted");


	private String value;

	UserStatus(String value) {
		this.value = value;
	}
}

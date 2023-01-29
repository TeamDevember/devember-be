package com.gridians.girdians.domain.card.type;

public enum CardStatus {
	ACTIVE("active"), UNACTIVE("unactive"), BLOCKED("block");


	private String value;

	CardStatus(String value) {
		this.value = value;
	}
}

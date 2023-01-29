package com.gridians.girdians.domain.card.exception;

import com.gridians.girdians.domain.card.type.CardErrorCode;


public class CardException extends RuntimeException{

	private CardErrorCode cardErrorCode;

	public CardException(CardErrorCode cardErrorCode) {
		super(cardErrorCode.getDescription());
	}
}

package com.gridians.gridians.domain.card.exception;

import com.gridians.gridians.domain.card.type.CardErrorCode;


public class CardException extends RuntimeException{

	private CardErrorCode cardErrorCode;

	public CardException(CardErrorCode cardErrorCode) {
		super(cardErrorCode.getDescription());
	}
}

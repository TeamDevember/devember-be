package com.devember.devember.card.exception;

import com.devember.devember.card.type.CardErrorCode;


public class CardException extends RuntimeException{

	private CardErrorCode cardErrorCode;

	public CardException(CardErrorCode cardErrorCode) {
		super(cardErrorCode.getDescription());
	}
}

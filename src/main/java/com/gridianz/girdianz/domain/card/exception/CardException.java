package com.gridianz.girdianz.domain.card.exception;

import com.gridianz.girdianz.domain.card.type.CardErrorCode;


public class CardException extends RuntimeException{

	private CardErrorCode cardErrorCode;

	public CardException(CardErrorCode cardErrorCode) {
		super(cardErrorCode.getDescription());
	}
}

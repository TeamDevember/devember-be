package com.gridianz.girdianz.domain.card.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardErrorCode {

	CARD_NOT_FOUND("카드를 찾을 수 없습니다."),
	DUPLICATED_USER("이미 등록되어 있는 유저입니다."),
	DUPLICATED_SKILL("이미 등록되어 있는 스킬입니다."),
	OVERLAP_STATUS("이미 카드의 상태는 해당 코드와 같습니다.");

	private final String description;

}

package com.gridians.gridians.domain.user.type;


import lombok.Getter;

import java.util.UUID;

@Getter
public enum MailMessage {
	EMAIL_AUTH_MESSAGE("인증 메일이 전송되었습니다."),

	EMAIL_CONTENT_MESSAGE_FRONT("<p>아래 링크를 클릭하셔서 가입을 완료하세요</p><div><a href=\"https://gridians.site/certification?id="),
	EMAIL_CONTENT_MESSAGE_BACK("\">인증</a></div>"),
	EMAIL_PASSWORD_MESSAGE("패스워드 변경 이메일"),
	EMAIL_CONTENT_MESSAGE_PASSWORD_FRONT("<p>비밀번호가 새로 발급되었습니다.</p><p>"),
	EMAIL_CONTENT_MESSAGE_PASSWORD_BACK("</p>"),
	EMAIL_EMAIL_UPDATE("이메일 변경 인증 이메일"),
	EMAIL_CONTENT_MESSAGE_EMAIL_FRONT("</p>이메일이 변경 되었습니다. 아래 링크를 눌러 이메일 변경을 마무리 하세요" +
			"</p><div><a href=\"https://gridians.site/certification?email="),
	EMAIL_CONTENT_MESSAGE_EMAIL_BACK("\">이메일 변경</a></div>");
	private String value;

	MailMessage(String value) {
		this.value = value;
	}

	public static String setContentMessage(UUID id) {
		return EMAIL_CONTENT_MESSAGE_FRONT.getValue() + id + EMAIL_CONTENT_MESSAGE_BACK.getValue();
	}

	public static String setPasswordContentMessage(String id) {
		return EMAIL_CONTENT_MESSAGE_PASSWORD_FRONT.getValue() + id + EMAIL_CONTENT_MESSAGE_BACK.getValue();
	}

	public static String setEmailUpdateMessage(String email) {
		return EMAIL_CONTENT_MESSAGE_EMAIL_FRONT.getValue() + email + EMAIL_CONTENT_MESSAGE_EMAIL_BACK.getValue();
	}
}

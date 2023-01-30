package com.gridians.gridians.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LoginDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		private String email;
		private String password;
	}


	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ValidResponse {

		private String nickname;
		private int passwordLength;
		private String githubId;
		private String email;

		public static ValidResponse from(String nickname, int passwordLength, String email) {
			return ValidResponse.builder()
                    .nickname(nickname)
                    .passwordLength(passwordLength)
                    .email(email)
					.build();
		}

	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {
		private String token;

		public static Response from(String token) {
			return Response.builder()
					.token(token)
					.build();
		}

		public static Response socialFrom(String token) {
			return Response.builder()
					.token(token)
					.build();
		}

	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SocialRequest {
		private String token;
	}
}

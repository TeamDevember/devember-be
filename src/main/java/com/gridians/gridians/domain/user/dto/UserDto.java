package com.gridians.gridians.domain.user.dto;


import com.gridians.gridians.domain.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public class UserDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		private String nickname;
		private String password;
		private String updatePassword;
		private String email;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class deleteRequest {
		private String password;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {
		private String email;
		private String nickname;
		private String profileImage;

		public static Response from(User user) {
			return Response.builder()
					.nickname(user.getNickname())
					.email(user.getEmail())
					.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class RequestToken {
		private String refreshToken;
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class ResponseToken {
		private String accessToken;
		public static ResponseToken from(String accessToken) {
			return ResponseToken.builder()
					.accessToken(accessToken)
					.build();
		}
	}
}

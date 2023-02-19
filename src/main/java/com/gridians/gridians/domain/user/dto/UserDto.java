package com.gridians.gridians.domain.user.dto;


import com.gridians.gridians.domain.user.entity.User;
import lombok.*;

@Getter
public class UserDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateRequest {
		private String nickname;
		private String password;
		private String updatePassword;
		private String email;
	}

	@Getter
	public static class DeleteRequest {
		private String password;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DefaultResponse {
		private String email;
		private String nickname;
		private String profileImage;

		public static DefaultResponse from(User user) {
			return DefaultResponse.builder()
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
		private String accessToken;
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

package com.gridians.gridians.domain.user.dto;


import com.gridians.gridians.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class UserDto {

	@Builder
	@Getter
	public static class JoinResponse {
		private String nickname;
		private LocalDateTime createdAt;

		public static JoinResponse from(User user) {
			return JoinResponse.builder()
					.nickname(user.getNickname())
					.createdAt(user.getCreatedAt())
					.build();
		}
	}

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
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NowDataResponse {
		private String nickname;
		private String email;

		public static NowDataResponse from(String nickname, String email) {
			return NowDataResponse.builder()
					.nickname(nickname)
					.email(email)
					.build();
		}
	}

}

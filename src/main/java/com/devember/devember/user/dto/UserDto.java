package com.devember.devember.user.dto;


import com.devember.devember.user.entity.User;
import com.devember.devember.user.type.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserDto {
	@Builder
	@Getter
	public static class JoinResponse{
		private String name;
		private String nickname;
		private LocalDateTime createdAt;

		public static JoinResponse from(User user){
			return JoinResponse.builder()
					.name(user.getName())
					.nickname(user.getName())
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


	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {
		private String result;

		public static Response PasswordNotMatch() {
			return Response.builder().result("fail").build();
		}
	}
}

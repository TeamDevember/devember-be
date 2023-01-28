package com.gridianz.girdianz.domain.user.dto;


import com.gridianz.girdianz.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
		private String updatePassword;
		private String email;
	}
}

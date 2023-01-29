package com.gridians.gridians.domain.user.dto;


import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.type.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private UUID id;

	private String email;
	private String nickname;
	private String password;

	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	private UserStatus userStatus;


	@Builder
	@Getter
	public static class JoinResponse{
		private String nickname;
		private LocalDateTime createdAt;

		public static JoinResponse from(User user){
			return JoinResponse.builder()
					.nickname(user.getNickname())
					.createdAt(user.getCreatedAt())
					.build();
		}
	}
}

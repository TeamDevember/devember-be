package com.gridians.gridians.domain.user.dto;

import com.gridians.gridians.domain.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class JoinDto {

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		@Email
		private String email;
		@NotBlank
		private String nickname;
		@NotBlank
		private String password;
		private Long githubNumberId;
	}

	@Builder
	@Getter
	@Setter
	public static class Response {
		private String nickname;

		public static Response from(User user){
			return Response.builder()
					.nickname(user.getNickname())
					.build();
		}
	}
}

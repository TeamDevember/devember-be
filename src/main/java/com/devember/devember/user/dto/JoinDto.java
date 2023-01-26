package com.devember.devember.user.dto;

import com.devember.devember.user.entity.User;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
public class JoinDto {

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		private String email;
		private String nickname;
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
					.nickname(user.getName())
					.build();
		}
	}
}

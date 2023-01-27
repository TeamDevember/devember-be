package com.gridianz.girdianz.domain.user.dto;

import com.gridianz.girdianz.domain.user.entity.User;
import lombok.*;

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

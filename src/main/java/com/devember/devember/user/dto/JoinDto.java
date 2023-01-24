package com.devember.devember.user.dto;

import com.devember.devember.user.entity.User;
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

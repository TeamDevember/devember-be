package com.gridians.gridians.domain.user.dto;

import com.gridians.gridians.domain.user.entity.User;
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

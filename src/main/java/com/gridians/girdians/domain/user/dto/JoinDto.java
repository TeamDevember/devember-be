package com.gridians.girdians.domain.user.dto;

import com.gridians.girdians.domain.user.entity.User;
import lombok.*;

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
					.nickname(user.getName())
					.build();
		}
	}
}

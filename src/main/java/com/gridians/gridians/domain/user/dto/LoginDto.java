package com.gridians.gridians.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LoginDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		private String email;
		private String password;
	}

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String token;
        private String accessToken;
        private String refreshToken;
        private String nickname;
        private int passwordLength;
        private String githubId;
        private String email;

        public static Response from(String accessToken, String refreshToken) {
            return Response.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

		public static Response socialFrom(String token) {
			return Response.builder()
					.token(token)
					.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SocialRequest {
		private String token;
	}
}

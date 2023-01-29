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
        private String nickname;
        private int passwordLength;
        private String githubId;
        private String email;

        public static Response from(String token, String nickname, String email, int passwordLength) {
            return Response.builder()
                    .nickname(nickname)
                    .email(email)
                    .passwordLength(passwordLength)
                    .token(token)
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

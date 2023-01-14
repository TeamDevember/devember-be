package com.devember.devember.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginDto {

    @Getter
    @Builder
    public static class Request {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class Response {
        private String token;
    }

    @Getter
    @Builder
    public static class SocialRequest {
        private String token;
        private String status;
    }
}

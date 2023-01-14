package com.devember.devember.user.service;

import com.devember.global.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SocialRequest {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo";
    private static final String GITHUB_USER_INFO_URL = "https://api.github.com/user";
    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${github.client-id}")
    private String githubClientId;
    @Value("${github.client-secret}")
    private String gitHubClientSecret;
    @Value("${github.redirect-uri}")
    private String githubRedirectUri;

    public String kakaoRequest(String token) throws Exception {
        String accessToken = getKakaoAccessToken(token);
        String email = getKakaoEmail(accessToken);

        return email;
    }

    private String getKakaoEmail(String accessToken) throws Exception{
        Map<String, String> headers = new HashMap<>();

        headers.put("Authorization", "Bearer " + accessToken);

        String response = ApiUtils.requestWithHeader(KAKAO_USER_INFO_URL, "POST", headers);
        JSONObject jsonObject = new JSONObject(response);
        String email = jsonObject.getJSONObject("kakao_account").getString("email");
        return email;
    }
    private String getKakaoAccessToken(String token) throws Exception{
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        parameters.put("grant_type", "authorization_code");
        parameters.put("client_id", kakaoClientId);
        parameters.put("redirect_uri", kakaoRedirectUri);
        parameters.put("code", token);

        String response = ApiUtils.requestWithHeaderAndParam(KAKAO_ACCESS_TOKEN_URL, "POST", headers, parameters);

        JSONObject jsonObject = new JSONObject(response);
        String accessToken = jsonObject.getString("access_token");
        return accessToken;
    }

    public String googleRequest(String token) throws Exception{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", token);

        String response = ApiUtils.requestWithParamAndNoHeader(GOOGLE_USER_INFO_URL, "GET", parameters);
        JSONObject jsonObject = new JSONObject(response);
        String email = jsonObject.getString("email");

        return email;
    }

    public String githubRequest(String token) throws Exception {
        String accessToken = getGithubAccessToken(token);
        String email = getGitHubEmail(accessToken);

        return email;
    }

    private String getGitHubEmail(String accessToken) throws Exception{
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.github+json");
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("X-GitHub-Api-Version", "2022-11-28");

        String response = ApiUtils.requestWithHeader(GITHUB_USER_INFO_URL, "GET", headers);
        String email = "";

        JSONObject jsonObject = new JSONObject(response);
        Object emailObject = jsonObject.get("email");
        if(emailObject == null){
            log.info("null");
            email = jsonObject.getString("login");
        }
        else {
            email = emailObject.toString();
        }

        return email;
    }

    private String getGithubAccessToken(String token) throws Exception {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        headers.put("Accept", "application/json");
        parameters.put("client_id", githubClientId);
        parameters.put("client_secret", gitHubClientSecret);

        String response = ApiUtils.requestWithHeaderAndParam(GITHUB_ACCESS_TOKEN_URL, "POST", headers, parameters);

        log.info("git response = {}", response);
        JSONObject jsonObject = new JSONObject(response);
        String accessToken = jsonObject.getString("access_token");
        return accessToken;
    }
}

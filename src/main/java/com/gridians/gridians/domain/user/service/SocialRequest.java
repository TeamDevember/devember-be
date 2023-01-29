package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.global.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SocialRequest {

    private static final String GITHUB_USER_INFO_URL = "https://api.github.com/user";
    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private JSONParser jsonParser = new JSONParser();

    @Value("${github.client-id}")
    private String githubClientId;
    @Value("${github.client-secret}")
    private String gitHubClientSecret;


    public String githubRequest(String token) throws Exception {
        String accessToken = getGithubAccessToken(token);
        return getGitHubId(accessToken);

    }

    public String getGitHubId(String accessToken) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.github+json");
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("X-GitHub-Api-Version", "2022-11-28");

        String response = ApiUtils.requestWithHeader(GITHUB_USER_INFO_URL, "GET", headers);

        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
        return (String) jsonObject.get("id");
    }

    private String getGithubAccessToken(String token) throws Exception {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        headers.put("Accept", "application/json");
        parameters.put("client_id", githubClientId);
        parameters.put("client_secret", gitHubClientSecret);
        parameters.put("code", token);

        String response = ApiUtils.requestWithHeaderAndParam(GITHUB_ACCESS_TOKEN_URL, "POST", headers, parameters);

        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
        String accessToken = (String) jsonObject.get("access_token");
        return accessToken;
    }
}

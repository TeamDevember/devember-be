package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.global.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubService {
    private static final String GITHUB_USER_INFO_WITH_ID = "https://api.github.com/users/";

    private static final String GITHUB_USER_INFO_URL_WITH_TOKEN = "https://api.github.com/user";
    private static final String GITHUB_GET_ACCESS_TOKEN_URL_WITH_CODE = "https://github.com/login/oauth/access_token";
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

        String response = ApiUtils.requestWithHeader(GITHUB_USER_INFO_URL_WITH_TOKEN, "GET", headers);

        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
        String ret = jsonObject.get("id").toString();
        return ret;
    }

    private String getGithubAccessToken(String token) throws Exception {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        headers.put("Accept", "application/json");
        parameters.put("client_id", githubClientId);
        parameters.put("client_secret", gitHubClientSecret);
        parameters.put("code", token);

        String response = ApiUtils.requestWithHeaderAndParam(GITHUB_GET_ACCESS_TOKEN_URL_WITH_CODE, "POST", headers, parameters);

        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
        String accessToken = (String) jsonObject.get("access_token");
        return accessToken;
    }
}

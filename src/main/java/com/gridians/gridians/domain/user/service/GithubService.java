package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.user.dto.GithubDto;
import com.gridians.gridians.domain.user.entity.Github;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.GithubRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import com.gridians.gridians.global.error.exception.EntityNotFoundException;
import com.gridians.gridians.global.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private final UserRepository userRepository;
    private final GithubRepository githubRepository;

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

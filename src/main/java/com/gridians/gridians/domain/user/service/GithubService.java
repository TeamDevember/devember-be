package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.user.dto.GithubDto;
import com.gridians.gridians.domain.user.entity.Github;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.GithubRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.error.exception.ErrorCode;
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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubService {
    private static final String GITHUB_USER_INFO_WITH_ID = "https://api.github.com/users/";
    private static final String GITHUB_USER_INFO_URL_WITH_TOKEN = "https://api.github.com/user";
    private static final String GITHUB_GET_ACCESS_TOKEN_URL_WITH_CODE = "https://github.com/login/oauth/access_token";
    private JSONParser jsonParser = new JSONParser();

    private final GithubRepository githubRepository;
    private final UserRepository userRepository;

    @Value("${github.client-id}")
    private String githubClientId;
    @Value("${github.client-secret}")
    private String gitHubClientSecret;

    @Value("${custom.path.github}")
    private String githubApi;

    private String separator = "/";


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

    public GithubDto parsing(Long githubId) throws Exception {
        JSONParser parser = new JSONParser();

        URL mainUrl = new URL(githubApi + separator + githubId);
        String gitInfo = ApiUtils.request(mainUrl.toString(), "GET");
        JSONObject gitInfoObject = (JSONObject) parser.parse(gitInfo);

        URL subUrl = new URL(githubApi + separator + githubId + "/events");
        String gitEventInfo = ApiUtils.request(subUrl.toString(), "GET");

        JSONArray jsonArray = (JSONArray) parser.parse(gitEventInfo);
        String message = "";
        String date = "";


        for (Object o : jsonArray) {
            JSONObject o2 = (JSONObject) o;

            if (o2.get("type").equals("PushEvent")) {
                date = (String) o2.get("created_at");
                Object payload = o2.get("payload");
                JSONObject payload1 = (JSONObject) payload;
                Object commits = payload1.get("commits");
                JSONArray commits1 = (JSONArray) commits;

                if (commits1.size() > 0) {
                    Object o3 = commits1.get(0);
                    JSONObject o31 = (JSONObject) o3;
                    message = (String) o31.get("message");
                    break;
                }
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate realDate = simpleDateFormat.parse(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return GithubDto.builder()
                .name((String) gitInfoObject.get("name"))
                .login((String) gitInfoObject.get("login"))
                .githubId((Long) gitInfoObject.get("id"))
                .githubUrl((String) gitInfoObject.get("url"))
                .following((Long) gitInfoObject.get("following"))
                .followers((Long) gitInfoObject.get("followers"))
                .location((String) gitInfoObject.get("location"))
                .imageUrl((String) gitInfoObject.get("avatar_url"))
                .recentCommitAt(realDate)
                .recentCommitMessage(message)
                .build();
    }

    @Transactional
    public void deleteGithub(String email) {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        Github findGithub = githubRepository.findByUser(findUser)
                .orElseThrow(() -> new UserException(ErrorCode.GITHUB_NOT_FOUND));
        githubRepository.delete(findGithub);
    }

    @Transactional
    public void initGithub(String email, Long githubId){
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        findUser.setGithubNumberId(githubId);
        Optional<Github> optionalGithub = githubRepository.findByUser(findUser);

        if(optionalGithub.isPresent()){
            githubRepository.delete(optionalGithub.get());
        }

        try {
            Github github = Github.from(parsing(githubId));
            github.setUser(findUser);
            findUser.setGithub(github);
            githubRepository.save(github);
        }catch (Exception e){
            throw new RuntimeException("잠시 후에 다시 등록해주세요");
        }
    }

    @Transactional
    public void updateGithub(String email, Long githubId){
        if (githubId != null && userRepository.findByGithubNumberId(githubId).isPresent()) {
            throw new UserException(ErrorCode.DUPLICATED_GITHUB_ID);
        }
        initGithub(email, githubId);
    }
}

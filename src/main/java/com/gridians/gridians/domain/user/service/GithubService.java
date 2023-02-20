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

    public GithubDto parsing(String githubId) throws IOException, ParseException, java.text.ParseException {
        JSONParser parser = new JSONParser();

        URL mainUrl = new URL(githubApi + separator + githubId);

        BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
        String result = br.readLine();
        JSONObject o1 = (JSONObject) parser.parse(result);

        URL subUrl = new URL(githubApi + separator + githubId + "/events");
        BufferedReader subBr = new BufferedReader(new InputStreamReader(subUrl.openStream(), StandardCharsets.UTF_8));
        String subResult = subBr.readLine();

        JSONArray jsonArray = (JSONArray) parser.parse(subResult);
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
                .name((String) o1.get("name"))
                .login((String) o1.get("login"))
                .githubId((Long) o1.get("id"))
                .githubUrl((String) o1.get("url"))
                .following((Long) o1.get("following"))
                .followers((Long) o1.get("followers"))
                .location((String) o1.get("location"))
                .imageUrl((String) o1.get("avatar_url"))
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
    public void updateGithub(String email, String githubId){
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

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
}

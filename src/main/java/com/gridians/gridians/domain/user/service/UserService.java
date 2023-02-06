package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.dto.GithubDto;
import com.gridians.gridians.domain.card.entity.Github;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.GithubRepository;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.card.type.CardErrorCode;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.*;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.TokenRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.MailMessage;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.MailComponent;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.CustomJwtException;
import com.gridians.gridians.global.error.exception.EntityNotFoundException;
import com.gridians.gridians.global.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileCardRepository profileCardRepository;
    private final FavoriteRepository favoriteRepository;
    private final MailComponent mailComponent;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;
    private final GithubRepository githubRepository;
    private final SocialRequest socialRequest;

    @Transactional
    public User signUp(JoinDto.Request request) throws RuntimeException {
        User user = User.from(request);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        User savedUser;

        if (optionalUser.isPresent()) { //중복 이메일
            savedUser = optionalUser.get();
            throw new DuplicateEmailException(savedUser.getEmail());
        } else {
            user.setUserStatus(UserStatus.UNACTIVE);
            user.setRole(Role.ANONYMOUS);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setNickname(user.getNickname());
            savedUser = userRepository.save(user);
            mailComponent.sendMail(user.getEmail(), MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setContentMessage(savedUser.getId()));

            return savedUser;
        }
    }

    @Transactional
    public JoinDto.Response joinAuth(String id) {
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.setRole(Role.USER);
        user.setUserStatus(UserStatus.ACTIVE);

        return JoinDto.Response.from(userRepository.save(user));
    }

    public void verifyUser(String email, String password) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("password not match");
        }
        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
        }
        if(user.getUserStatus() == UserStatus.DELETED) {
            throw new UserDeleteException("deleted user");
        }
    }

    public LoginDto.Response login(Authentication authentication) {
        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken(authentication);

        String email = ((JwtUserDetails) authentication.getPrincipal()).getEmail();
        tokenRepository.save(refreshToken, email, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());

        return LoginDto.Response.from(accessToken, refreshToken);
    }

    public String issueAccessToken(String refreshToken) {
        String issuedAccessToken = "";
        try {
            if(StringUtils.hasText(refreshToken) && tokenRepository.hasKeyToken(refreshToken)) {
                Authentication authentication = jwtUtils.getAuthenticationByToken(refreshToken);
                issuedAccessToken = jwtUtils.createAccessToken(authentication);
            }
        } catch (Exception e) {
            throw new CustomJwtException("no refresh key");
        }

        return issuedAccessToken;
    }

    public String createAccessToken(Authentication authentication) {
        return jwtUtils.createAccessToken(authentication);
    }

    public String createRefreshToken(Authentication authentication) {
        return jwtUtils.createRefreshToken(authentication);
    }

    public void logout(String accessToken, String refreshToken) {
        String email = jwtUtils.getUserEmailFromToken(refreshToken);
        tokenRepository.saveBlackList(accessToken, email, jwtUtils.ACCESS_TOKEN_EXPIRE_TIME.intValue());
        tokenRepository.saveBlackList(refreshToken, email, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());
    }

    public boolean checkUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void addFavorite(String email, String favorUserEmail) {
        User user = getUserByEmail(email);
        User favorUser = getUserByEmail(favorUserEmail);

        profileCardRepository.findByUser(favorUser)
                .orElseThrow(() -> new RuntimeException("profile card not found"));

        Favorite favorite = Favorite.builder()
                .user(favorUser)
                .build();
        user.addFavorite(favorite);
        User savedUser = userRepository.save(user);
    }

    @Transactional
    public void deleteFavorite(String email, String favorUserEmail) {
        User user = getUserByEmail(email);
        User favorUser = getUserByEmail(favorUserEmail);

        Favorite favorite = favoriteRepository.findByUser(favorUser)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        user.deleteFavorite(favorite);
        favoriteRepository.deleteById(favorite.getId());
    }


    @Transactional
    public Authentication socialLogin(String token) throws Exception {
        Long githubId = Long.valueOf(socialRequest.githubRequest(token));

        User user = userRepository.findByGithubNumberId(githubId)
                .orElseThrow(() -> new GithubIdNotFoundException("user not found", githubId.toString()));

        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
        }

        return jwtUtils.getAuthenticationByEmail(user.getEmail());
    }

    public void verifyUserPassword(String email, String password) {
        User user = getUserByEmail(email);
        verifyPassword(password, user.getPassword());
    }

    @Transactional
    public void deleteUser(String userEmail, String password) {
        User user = getUserByEmail(userEmail);
        if(!verifyPassword(password, user.getPassword())){
            log.info("password not match");
            throw new PasswordNotMatchException("password not match");
        }

        user.setUserStatus(UserStatus.DELETED);
    }

    @Transactional
    public void updateEmail(String userEmail, String updateEmail) {
        User user = getUserByEmail(userEmail);
        user.setEmail(updateEmail);
    }

    @Transactional
    public void updateUser(String userEmail, UserDto.Request userDto) {
        User user = getUserByEmail(userEmail);

        user.setNickname(userDto.getNickname());

        if(!userDto.getPassword().isEmpty()){
            if(verifyPassword(userDto.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDto.getUpdatePassword()));
            }
        }
    }

    @Transactional
    public void findPassword(String email) {
        String uuid = UUID.randomUUID().toString();
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(uuid));

        mailComponent.sendPasswordMail(email, MailMessage.EMAIL_PASSWORD_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
    }

    public void sendUpdateEmail(String userEmail, String updateEmail) {
        mailComponent.sendUpdateEmail(updateEmail, MailMessage.EMAIL_EMAIL_UPDATE, MailMessage.setEmailUpdateMessage(updateEmail));
    }


    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email + "not found"));
    }


    private boolean verifyPassword(String rawPassword, String cryptPassword) {
        if (!passwordEncoder.matches(rawPassword, cryptPassword)) {
            throw new PasswordNotMatchException("password not match");
        }

        return true;
    }

    public User getUserInfo(String userEmail) {
        return getUserByEmail(userEmail);
    }

    @Transactional
    public void saveGithub(String email, GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Github github = Github.from(parsing(request.getGithubId()));
        github.setUser(user);
        Github savedGithub = githubRepository.save(github);
        user.setGithub(savedGithub);
    }

    public void deleteGithub(String email, GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Github github = githubRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        githubRepository.delete(github);
    }

    public GithubDto parsing(String githubId) throws IOException, ParseException, java.text.ParseException {
        JSONParser parser = new JSONParser();

        URL mainUrl = new URL("https://api.github.com/users/" + githubId);

        BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
        String result = br.readLine();
        JSONObject o1 = (JSONObject) parser.parse(result);

        URL subUrl = new URL("https://api.github.com/users/" + githubId + "/events");
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

}

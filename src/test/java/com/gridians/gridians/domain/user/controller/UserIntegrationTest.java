package com.gridians.gridians.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridians.gridians.domain.user.WithCustomMockUser;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.repository.TokenRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserController userController;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TokenRepository tokenRepository;


    User verifyUser;
    User notVerifyUser;
    String accessToken;
    String rawPassword = "password";


    @BeforeEach
    public void beforeEach() throws Exception {
        UUID uuid = UUID.randomUUID();
        verifyUser = User.builder()
                .id(uuid)
                .email("verifyUser@email.com")
                .nickname("verifyUserNickname")
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        notVerifyUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .nickname("nickname")
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ANONYMOUS)
                .userStatus(UserStatus.UNACTIVE)
                .build();

        accessToken = jwtUtils.createAccessToken(JwtUserDetails.create(verifyUser));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void singUpTest() throws Exception {
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email(notVerifyUser.getEmail())
                .nickname(notVerifyUser.getNickname())
                .password("password12!")
                .build();

        mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").value(notVerifyUser.getNickname()))
        ;

        Optional<User> optionalUser = userRepository.findByEmail(requestDto.getEmail());
        assertThat(optionalUser).isNotEmpty();

        User user = optionalUser.get();
        assertThat(user.getEmail()).isEqualTo(notVerifyUser.getEmail());
        assertThat(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).isTrue();
    }

    public ResultActions errorResponseExpect(ResultActions resultActions, ErrorCode errorCode) throws Exception {
        return resultActions
                .andExpect(jsonPath("message").value(errorCode.getMessage()))
                .andExpect(jsonPath("status").value(errorCode.getStatus()))
                .andExpect(jsonPath("code").value(errorCode.getCode()));
    }

    @Test
    @DisplayName("회원가입 테스트 - 이메일 중복")
    public void signUpEmailDuplicateTest() throws Exception {
        userRepository.save(User.builder()
                .email(notVerifyUser.getEmail())
                .build());
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email(notVerifyUser.getEmail())
                .nickname(notVerifyUser.getNickname())
                .password(notVerifyUser.getPassword())
                .build();

        mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()))
                .andExpect(jsonPath("status").value(ErrorCode.DUPLICATE_EMAIL.getStatus()))
                .andExpect(jsonPath("code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
                .andExpect(jsonPath("param").isEmpty())
                .andExpect(jsonPath("errors").isEmpty())
        ;

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getEmail()).isEqualTo(notVerifyUser.getEmail());
    }

    @Test
    @DisplayName("회원가입 테스트 - 닉네임 중복")
    public void signUpNicknameDuplicateTest() throws Exception {
        userRepository.save(User.builder()
                .email("test@email.com")
                .nickname(notVerifyUser.getNickname())
                .build());

        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email(notVerifyUser.getEmail())
                .nickname(notVerifyUser.getNickname())
                .password("password")
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        errorResponseExpect(resultActions, ErrorCode.DUPLICATED_NICKNAME)
                .andExpect(jsonPath("param").isEmpty())
                .andExpect(jsonPath("errors").isEmpty());

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getEmail()).isNotEqualTo(notVerifyUser.getNickname());
    }

    @Test
    @DisplayName("회원가입 테스트 - 입력 공백 테스트")
    public void signUpEmptyTest() throws Exception {
        JoinDto.Request requestDto = JoinDto.Request.builder().build();

        ResultActions resultActions = mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.INVALID_INPUT_VALUE);

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("로그인 테스트")
    public void loginTest() throws Exception {
        userRepository.save(verifyUser);
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email(verifyUser.getEmail())
                .password(rawPassword)
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").isNotEmpty())
                .andExpect(jsonPath("refreshToken").isNotEmpty())
                .andExpect(jsonPath("nickname").value(verifyUser.getNickname()));

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        LoginDto.Response response = objectMapper.readValue(contentAsString, LoginDto.Response.class);
        assertThat(response.getNickname()).isEqualTo(verifyUser.getNickname());
        assertThat(jwtUtils.validateToken(response.getAccessToken())).isTrue();
        assertThat(jwtUtils.validateToken(response.getRefreshToken())).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    public void loginPasswordNotMatchTest() throws Exception {
        userRepository.save(verifyUser);
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email(verifyUser.getEmail())
                .password("test!")
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
    }

    @Test
    @DisplayName("로그인 테스트 - 존재하지 않는 유저")
    public void userLoginNotExistUserTest() throws Exception {
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email("none@email.com")
                .password(rawPassword)
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.ENTITY_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 테스트 - 이메일 인증되지 않은 유저")
    public void userLoginNotVerifyUserTest() throws Exception {
        userRepository.save(notVerifyUser);
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email("email@email.com")
                .password(rawPassword)
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());

        errorResponseExpect(resultActions, ErrorCode.EMAIL_NOT_VERIFIED);
    }

    @Test
    @DisplayName("유저 이메일 인증")
    public void userEmailVerify() throws Exception {
        User savedUser = userRepository.save(notVerifyUser);

        mvc.perform(get("/user/auth/email-auth")
                .param("id", savedUser.getId().toString()))
                .andExpect(status().isOk());

        User findUser = userRepository.findById(savedUser.getId()).get();
        assertThat(findUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(findUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("유저 이메일 변경")
    public void userEmailUpdateTest() throws Exception {
        userRepository.save(verifyUser);
        String updateEmail = "updateEmail@email.com";
        UserDto.UpdateRequest userDto = UserDto.UpdateRequest.builder()
                .email(updateEmail)
                .build();

        mvc.perform(put("/user/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(userDto))
                .header("Authorization", "Bearer: " + accessToken))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.findByEmail(verifyUser.getEmail());
        assertThat(optionalUser).isEmpty();

        Optional<User> updateOptionalUser = userRepository.findByEmail(updateEmail);
        assertThat(updateOptionalUser).isNotEmpty();

        User user = updateOptionalUser.get();
        assertThat(user.getEmail()).isEqualTo(updateEmail);
        assertThat(user.getEmail()).isNotEqualTo(verifyUser.getEmail());
    }

    @Test
    @DisplayName("유저 정보 변경 - 닉네임 변경")
    public void userNicknameUpdateTest() throws Exception {
        userRepository.save(verifyUser);
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("updateNickname")
                .build();

        ResultActions resultActions = mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .header("Authorization", "Bearer: " + accessToken)
                        .with(csrf()))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(verifyUser.getEmail()).get();

        assertThat(user.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    @DisplayName("유저 정보 변경 - 패스워드 변경")
    public void userPasswordUpdateTest() throws Exception {
        userRepository.save(verifyUser);
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .password(rawPassword)
                .updatePassword("updatePassword")
                .build();

        mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .header("Authorization", "Bearer: " + accessToken)
                        .with(csrf()))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(verifyUser.getEmail()).get();

        assertThat(passwordEncoder.matches("updatePassword", user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("유저 정보 변경 - 패스워드 불일치")
    public void userUpdatePasswordNotMatchTest() throws Exception {
        userRepository.save(verifyUser);
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .password("test")
                .updatePassword("updatePassword")
                .build();

        ResultActions resultActions = mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .header("Authorization", "Bearer: " +accessToken)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);

        assertThat(passwordEncoder.matches("password", verifyUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("유저 탈퇴")
    public void deleteUserTest() throws Exception {
        userRepository.save(verifyUser);
        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
                .password(rawPassword)
                .build();

        mvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .header("Authorization", "Bearer: " + accessToken)
                        .with(csrf()))
                .andExpect(status().isOk())
        ;

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("유저 탈퇴 - 비밀번호 불일치")
    public void deleteUserPasswordNotMatch() throws Exception {
        userRepository.save(verifyUser);
        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
                .password("test")
                .build();

        ResultActions resultActions = mvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .header("Authorization", "Bearer: " + accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.WRONG_USER_PASSWORD.getStatus()));
        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);

        List<User> users = userRepository.findAll();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUserStatus()).isNotEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("토큰 재발급")
    public void reissueTokenTest() throws Exception {
        userRepository.save(verifyUser);
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(verifyUser.getEmail(), rawPassword));
        String refreshToken = jwtUtils.createRefreshToken(authenticate);

        tokenRepository.save(accessToken, verifyUser.getEmail(), jwtUtils.getAccessTokenExpireTime().intValue());
        tokenRepository.save(refreshToken, verifyUser.getEmail(), jwtUtils.getRefreshTokenExpireTime().intValue());

        UserDto.RequestToken requestToken = UserDto.RequestToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        ResultActions resultActions = mvc.perform(post("/user/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestToken)))
                .andExpect(status().isOk());

        String result = resultActions.andReturn().getResponse().getContentAsString();
        UserDto.ResponseToken responseToken = objectMapper.readValue(result, UserDto.ResponseToken.class);

        assertThat(jwtUtils.validateToken(responseToken.getAccessToken())).isTrue();
        assertThat(jwtUtils.getUserEmailFromToken(responseToken.getAccessToken())).isEqualTo(verifyUser.getEmail());
        assertThat(tokenRepository.hasKeyToken(refreshToken)).isTrue();
    }
}

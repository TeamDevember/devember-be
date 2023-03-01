package com.gridians.gridians.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridians.gridians.domain.user.WithCustomMockUser;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.DuplicateEmailException;
import com.gridians.gridians.domain.user.exception.DuplicateNicknameException;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.GlobalExceptionHandler;
import com.gridians.gridians.global.error.exception.EntityNotFoundException;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = UserController.class)
@Import({UserController.class, JwtUserDetails.class, User.class, ObjectMapper.class})
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    UserService userService;
    @MockBean
    JwtUtils jwtUtils;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    UserRepository userRepository;
    @InjectMocks
    UserController userController;
    User verifyUser;
    User notVerifyUser;

    @BeforeEach
    public void beforeEach() throws Exception {
        UUID uuid = UUID.randomUUID();
        verifyUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .nickname("nickname")
                .password("encodedPassword")
                .role(Role.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        notVerifyUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .nickname("nickname")
                .password("encodedPassword")
                .role(Role.ANONYMOUS)
                .userStatus(UserStatus.UNACTIVE)
                .build();

        ReflectionTestUtils.setField(userController, "userService", userService);
        ReflectionTestUtils.setField(userController, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(userController, "authenticationManager", authenticationManager);

        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void singUpTest() throws Exception {
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email("email@email.com")
                .nickname("nickname")
                .password("password12!")
                .build();

        when(userService.signUp(any(JoinDto.Request.class))).thenReturn(notVerifyUser);

        mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").value(notVerifyUser.getNickname()))
        ;
    }

    @Test
    @DisplayName("회원가입 테스트 - 이메일 중복")
    public void signUpEmailDuplicateTest() throws Exception {
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email("email@email.com")
                .nickname("nickname")
                .password("password")
                .build();

        when(userService.signUp(any(JoinDto.Request.class))).thenThrow(new DuplicateEmailException(requestDto.getEmail()));

        mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()))
                .andExpect(jsonPath("status").value(ErrorCode.DUPLICATE_EMAIL.getStatus()))
                .andExpect(jsonPath("code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
                .andExpect(jsonPath("param").isEmpty())
                .andExpect(jsonPath("errors").isEmpty())
        ;
    }

    @Test
    @DisplayName("회원가입 테스트 - 닉네임 중복")
    public void signUpNicknameDuplicateTest() throws Exception {
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email("email@email.com")
                .nickname("nickname")
                .password("password")
                .build();

        when(userService.signUp(any(JoinDto.Request.class))).thenThrow(new DuplicateNicknameException(requestDto.getEmail()));

        ResultActions resultActions = mvc.perform(post("/user/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        errorResponseExpect(resultActions, ErrorCode.DUPLICATED_NICKNAME)
                .andExpect(jsonPath("param").isEmpty())
                .andExpect(jsonPath("errors").isEmpty());
        ;
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
    }

    @Test
    @DisplayName("로그인 테스트")
    @WithMockUser
    public void loginTest() throws Exception {
        String resAccessToken = "accessToken";
        String resRefreshToken = "refreshToken";

        LoginDto.Response res = LoginDto.Response.builder()
                .accessToken(resAccessToken)
                .refreshToken(resRefreshToken)
                .nickname(verifyUser.getNickname())
                .build();

        doNothing().when(userService).verifyUser(anyString(), anyString());
        when(userService.login(any())).thenReturn(res);

        mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(verifyUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").value(resAccessToken))
                .andExpect(jsonPath("refreshToken").value(resRefreshToken))
                .andExpect(jsonPath("nickname").value(verifyUser.getNickname()))
        ;
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    public void loginPasswordNotMatchTest() throws Exception {
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email("email@email.com")
                .password("test!")
                .build();

        doThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD)).when(userService).verifyUser(anyString(), anyString());

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
                .email("email@email.com")
                .password("test!")
                .build();

        doThrow(new UserException(ErrorCode.USER_NOT_FOUND)).when(userService).verifyUser(anyString(), anyString());

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 테스트 - 이메일 인증되지 않은 유저")
    public void userLoginNotVerifyUserTest() throws Exception {
        LoginDto.Request requestDto = LoginDto.Request.builder()
                .email("email@email.com")
                .password("test!")
                .build();

        doThrow(new UserException(ErrorCode.EMAIL_NOT_VERIFIED)).when(userService).verifyUser(anyString(), anyString());

        ResultActions resultActions = mvc.perform(post("/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());

        errorResponseExpect(resultActions, ErrorCode.EMAIL_NOT_VERIFIED);
    }

    public ResultActions errorResponseExpect(ResultActions resultActions, ErrorCode errorCode) throws Exception {
        return resultActions
                .andExpect(jsonPath("message").value(errorCode.getMessage()))
                .andExpect(jsonPath("status").value(errorCode.getStatus()))
                .andExpect(jsonPath("code").value(errorCode.getCode()));
    }

    @Test
    @DisplayName("유저 이메일 변경")
    public void userEmailUpdateTest() {

    }

    @Test
    @DisplayName("유저 정보 변경 - 닉네임 변경")
    @WithCustomMockUser
    public void userNicknameUpdateTest() throws Exception {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("nickname")
                .build();

        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenReturn(null);

        mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 정보 변경 - 패스워드 변경")
    @WithCustomMockUser
    public void userPasswordUpdateTest() throws Exception {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .password("password")
                .updatePassword("updatePassword")
                .build();

        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenReturn(null);

        mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 정보 변경 - 패스워드 불일치")
    @WithCustomMockUser
    public void userUpdatePasswordNotMatchTest() throws Exception {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .password("password")
                .updatePassword("updatePassword")
                .build();

        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD));

        ResultActions resultActions = mvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
    }

    @Test
    @DisplayName("유저 탈퇴")
    @WithCustomMockUser
    @Rollback
    public void deleteUserTest() throws Exception {
        userRepository.save(verifyUser);
        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
                .password("password12!")
                .build();

        JwtUserDetails jwtUserDetails = JwtUserDetails.create(verifyUser);

        doNothing().when(userService).deleteUser(jwtUserDetails.getEmail(), requestDto.getPassword());

        mvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("유저 탈퇴 - 비밀번호 불일치")
    @WithCustomMockUser
    public void deleteUserPasswordNotMatch() throws Exception {
        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
                .password("lll")
                .build();

        JwtUserDetails jwtUserDetails = JwtUserDetails.create(verifyUser);

        doThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD)).when(userService).deleteUser(anyString(), anyString());

        ResultActions resultActions = mvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.WRONG_USER_PASSWORD.getStatus()));
        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
    }
}

//package com.gridians.gridians.domain.user.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gridians.gridians.domain.user.WithCustomMockUser;
//import com.gridians.gridians.domain.user.dto.JoinDto;
//import com.gridians.gridians.domain.user.dto.LoginDto;
//import com.gridians.gridians.domain.user.dto.UserDto;
//import com.gridians.gridians.domain.user.entity.Role;
//import com.gridians.gridians.domain.user.entity.User;
//import com.gridians.gridians.domain.user.exception.DuplicateEmailException;
//import com.gridians.gridians.domain.user.exception.DuplicateNicknameException;
//import com.gridians.gridians.domain.user.exception.UserException;
//import com.gridians.gridians.domain.user.repository.UserRepository;
//import com.gridians.gridians.domain.user.service.UserService;
//import com.gridians.gridians.domain.user.type.UserStatus;
//import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
//import com.gridians.gridians.global.error.GlobalExceptionHandler;
//import com.gridians.gridians.global.error.exception.EntityNotFoundException;
//import com.gridians.gridians.global.error.exception.ErrorCode;
//import com.gridians.gridians.global.utils.JwtUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import javax.xml.transform.Result;
//import java.util.UUID;
//
//import static io.restassured.RestAssured.given;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ActiveProfiles({"test"})
//@WebAppConfiguration
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(value = UserController.class)
//@Import({UserController.class, JwtUserDetails.class, User.class, ObjectMapper.class})
//class UserControllerTest {
//
//    @Autowired
//    MockMvc mvc;
//    @Autowired
//    ObjectMapper objectMapper;
//    @InjectMocks
//    GlobalExceptionHandler globalExceptionHandler;
//
//    @MockBean
//    UserService userService;
//    @MockBean
//    JwtUtils jwtUtils;
//    @MockBean
//    AuthenticationManager authenticationManager;
//    @MockBean
//    UserRepository userRepository;
//    @InjectMocks
//    UserController userController;
//    User verifyUser;
//    User notVerifyUser;
//
//    @BeforeEach
//    public void beforeEach() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        verifyUser = User.builder()
//                .id(uuid)
//                .email("email@email.com")
//                .nickname("nickname")
//                .password("encodedPassword")
//                .role(Role.USER)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//
//        notVerifyUser = User.builder()
//                .id(uuid)
//                .email("email@email.com")
//                .nickname("nickname")
//                .password("encodedPassword")
//                .role(Role.ANONYMOUS)
//                .userStatus(UserStatus.UNACTIVE)
//                .build();
//
//        ReflectionTestUtils.setField(userController, "userService", userService);
//        ReflectionTestUtils.setField(userController, "jwtUtils", jwtUtils);
//        ReflectionTestUtils.setField(userController, "authenticationManager", authenticationManager);
//        mvc = MockMvcBuilders.standaloneSetup(userController)
//                .setControllerAdvice(globalExceptionHandler)
//                .build();
//    }
//
//    @Test
//    @DisplayName("회원가입 테스트")
//    public void singUpTest() throws Exception {
//        JoinDto.Request requestDto = JoinDto.Request.builder()
//                .email("email@email.com")
//                .nickname("nickname")
//                .password("password12!")
//                .build();
//
//        when(userService.signUp(any(JoinDto.Request.class))).thenReturn(notVerifyUser);
//
//        mvc.perform(post("/user/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("nickname").value(notVerifyUser.getNickname()))
//        ;
//    }
//
//    @Test
//    @DisplayName("회원가입 테스트 - 이메일 중복")
//    public void signUpEmailDuplicateTest() throws Exception {
//        JoinDto.Request requestDto = JoinDto.Request.builder()
//                .email("email@email.com")
//                .nickname("nickname")
//                .password("password")
//                .build();
//
//        when(userService.signUp(any(JoinDto.Request.class))).thenThrow(new DuplicateEmailException(requestDto.getEmail()));
//
//        mvc.perform(post("/user/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(requestDto)))
//                .andDo(print())
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()))
//                .andExpect(jsonPath("status").value(ErrorCode.DUPLICATE_EMAIL.getStatus()))
//                .andExpect(jsonPath("code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
//                .andExpect(jsonPath("param").isEmpty())
//                .andExpect(jsonPath("errors").isEmpty())
//        ;
//    }
//
//    @Test
//    @DisplayName("회원가입 테스트 - 닉네임 중복")
//    public void signUpNicknameDuplicateTest() throws Exception {
//        JoinDto.Request requestDto = JoinDto.Request.builder()
//                .email("email@email.com")
//                .nickname("nickname")
//                .password("password")
//                .build();
//
//        when(userService.signUp(any(JoinDto.Request.class))).thenThrow(new DuplicateNicknameException(requestDto.getEmail()));
//
//        ResultActions resultActions = mvc.perform(post("/user/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//
//        errorResponseExpect(resultActions, ErrorCode.DUPLICATED_NICKNAME)
//                .andExpect(jsonPath("param").isEmpty())
//                .andExpect(jsonPath("errors").isEmpty());
//        ;
//    }
//
//    @Test
//    @DisplayName("회원가입 테스트 - 입력 공백 테스트")
//    public void signUpEmptyTest() throws Exception {
//        JoinDto.Request requestDto = JoinDto.Request.builder().build();
//
//        ResultActions resultActions = mvc.perform(post("/user/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//        errorResponseExpect(resultActions, ErrorCode.INVALID_INPUT_VALUE);
//    }
//
//    @Test
//    @DisplayName("로그인 테스트")
//    @WithMockUser
//    public void loginTest() throws Exception {
//        String resAccessToken = "accessToken";
//        String resRefreshToken = "refreshToken";
//
//        LoginDto.Response res = LoginDto.Response.builder()
//                .accessToken(resAccessToken)
//                .refreshToken(resRefreshToken)
//                .nickname(verifyUser.getNickname())
//                .build();
//
//        doNothing().when(userService).verifyUser(anyString(), anyString());
//        when(userService.login(any())).thenReturn(res);
//
//        mvc.perform(post("/user/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(verifyUser))
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("accessToken").value(resAccessToken))
//                .andExpect(jsonPath("refreshToken").value(resRefreshToken))
//                .andExpect(jsonPath("nickname").value(verifyUser.getNickname()))
//        ;
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 패스워드 불일치")
//    public void loginPasswordNotMatchTest() throws Exception {
//        LoginDto.Request requestDto = LoginDto.Request.builder()
//                .email("email@email.com")
//                .password("test!")
//                .build();
//
//        doThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD)).when(userService).verifyUser(anyString(), anyString());
//
//        ResultActions resultActions = mvc.perform(post("/user/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().is4xxClientError());
//        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
//    }
//
//    @Test
//    @DisplayName("로그인 테스트 - 존재하지 않는 유저")
//    public void userLoginNotExistUserTest() throws Exception {
//        LoginDto.Request requestDto = LoginDto.Request.builder()
//                .email("email@email.com")
//                .password("test!")
//                .build();
//
//        doThrow(new UserException(ErrorCode.USER_NOT_FOUND)).when(userService).verifyUser(anyString(), anyString());
//
//        ResultActions resultActions = mvc.perform(post("/user/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().is4xxClientError());
//        errorResponseExpect(resultActions, ErrorCode.USER_NOT_FOUND);
//    }
//
//    @Test
//    @DisplayName("로그인 테스트 - 이메일 인증되지 않은 유저")
//    public void userLoginNotVerifyUserTest() throws Exception {
//        LoginDto.Request requestDto = LoginDto.Request.builder()
//                .email("email@email.com")
//                .password("test!")
//                .build();
//
//        doThrow(new UserException(ErrorCode.EMAIL_NOT_VERIFIED)).when(userService).verifyUser(anyString(), anyString());
//
//        ResultActions resultActions = mvc.perform(post("/user/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().isOk());
//
//        errorResponseExpect(resultActions, ErrorCode.EMAIL_NOT_VERIFIED);
//    }
//
//    public ResultActions errorResponseExpect(ResultActions resultActions, ErrorCode errorCode) throws Exception {
//        return resultActions
//                .andExpect(jsonPath("message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("status").value(errorCode.getStatus()))
//                .andExpect(jsonPath("code").value(errorCode.getCode()));
//    }
//
//    @Test
//    @DisplayName("유저 이메일 변경")
//    public void userEmailUpdateTest() {
//
//    }
//
//    @Test
//    @DisplayName("유저 정보 변경 - 닉네임 변경")
//    @WithCustomMockUser
//    public void userNicknameUpdateTest() throws Exception {
//        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
//                .nickname("nickname")
//                .build();
//
//        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenReturn(null);
//
//        mvc.perform(put("/user")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("유저 정보 변경 - 패스워드 변경")
//    @WithCustomMockUser
//    public void userPasswordUpdateTest() throws Exception {
//        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
//                .password("password")
//                .updatePassword("updatePassword")
//                .build();
//
//        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenReturn(null);
//
//        mvc.perform(put("/user")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("유저 정보 변경 - 패스워드 불일치")
//    @WithCustomMockUser
//    public void userUpdatePasswordNotMatchTest() throws Exception {
//        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
//                .password("password")
//                .updatePassword("updatePassword")
//                .build();
//
//        when(userService.updateUser(anyString(), any(UserDto.UpdateRequest.class))).thenThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD));
//
//        ResultActions resultActions = mvc.perform(put("/user")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().is4xxClientError());
//        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
//    }
//
//    @Test
//    @DisplayName("유저 탈퇴")
//    @WithCustomMockUser
//    public void deleteUserTest() throws Exception {
//        userRepository.save(verifyUser);
//        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
//                .password("password12!")
//                .build();
//
//        JwtUserDetails jwtUserDetails = JwtUserDetails.create(verifyUser);
//
//        doNothing().when(userService).deleteUser(jwtUserDetails.getEmail(), requestDto.getPassword());
//
//        mvc.perform(delete("/user")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//        ;
//    }
//
//    @Test
//    @DisplayName("유저 탈퇴 - 비밀번호 불일치")
//    @WithCustomMockUser
//    public void deleteUserPasswordNotMatch() throws Exception {
//        UserDto.DeleteRequest requestDto = UserDto.DeleteRequest.builder()
//                .password("lll")
//                .build();
//
//        JwtUserDetails jwtUserDetails = JwtUserDetails.create(verifyUser);
//
//        doThrow(new UserException(ErrorCode.WRONG_USER_PASSWORD)).when(userService).deleteUser(anyString(), anyString());
//
//        ResultActions resultActions = mvc.perform(delete("/user")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(new ObjectMapper().writeValueAsBytes(requestDto))
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().is(ErrorCode.WRONG_USER_PASSWORD.getStatus()));
//        errorResponseExpect(resultActions, ErrorCode.WRONG_USER_PASSWORD);
//    }
//}

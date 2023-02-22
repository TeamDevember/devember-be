package com.gridians.gridians.domain.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridians.gridians.domain.card.entity.Field;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.entity.Skill;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.security.filter.JwtAuthenticationFilter;
import com.gridians.gridians.global.config.security.service.CustomUserDetailsService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import groovy.util.logging.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileCardControllerTest {


	//Controller API를 테스트하기 위한 객체
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileCardRepository profileCardRepository;

	//Bean으로 등록할 구현체를 넣어야 함
	@MockBean
	ProfileCardService profileCardService;

	@MockBean
	UserService userService;

	@MockBean
	PasswordEncoder passwordEncoder;

	@MockBean
	JwtUtils jwtUtils;

	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	AuthenticationManager authenticationManager;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	ObjectMapper objectMapper;

	String rawPassword = "password12!";
	String accessToken;

	User verifyUser;
	User notVerifyUser;

	ProfileCard profileCard;

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

	@Test
	public void create() throws Exception {
		mockMvc.perform(post("/cards")
				.with(csrf())
				.header("Authorization", "Bearer " + accessToken))
				.andDo(print());
	}

	@Test
	public void input() throws Exception {

		profileCard = new ProfileCard();
		profileCard.setUser(verifyUser);
		profileCard.setStatusMessage("hi");

		mockMvc.perform(put("/cards/").with(csrf())
						.header("Authorization", "Bearer " + accessToken)
						.content(objectMapper.writeValueAsString(profileCard))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	}

	@Test
	public void deleteCard() throws Exception {

		profileCard = new ProfileCard();

		mockMvc.perform(delete("/cards").with(csrf())
						.header("Authorization", "Bearer " + accessToken)
						.content(objectMapper.writeValueAsString(profileCard))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	}

	@Test
	public void getMyCard() throws Exception {

		profileCard = new ProfileCard();

		mockMvc.perform(delete("/cards/my-card").with(csrf())
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(jsonPath("$[0]").exists())
				.andExpect(status().isOk());

	}


}

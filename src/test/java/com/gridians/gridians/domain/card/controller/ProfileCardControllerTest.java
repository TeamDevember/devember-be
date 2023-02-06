package com.gridians.gridians.domain.card.controller;

import com.gridians.gridians.domain.card.entity.Field;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.utils.JwtUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ProfileCardControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder passwordEncoder;

	String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2YWRmYmI1OS02YmI0LTQ0MzgtODUzMS05YzM4ZDQ0ZWRlNjEiLCJlbWFpbCI6ImRsd29kdWQ4MjFAZ21haWwuY29tIiwicm9sZSI6IltST0xFX1VTRVJdIiwiaWF0IjoxNjc1MTMzMzg5LCJleHAiOjE2NzUxMzUxODl9.aFniA2ZIcnH7H8st4CtAmE7XqWDOoN3AHOFjyP1AiqJBXHHovQD9BfgwRM9jOic8VTur5BNXi_SC6VsoYRYFLA";


	final String email = "dlwodud821@gmail.com";
	final String nickname = "jy";
	final String password = "dlwodud821!";

	@BeforeEach
	@Test
	public void init() throws Exception {
		User user = User.builder()
				.email(email)
				.nickname(nickname)
				.password(password)
				.userStatus(UserStatus.ACTIVE)
				.build();
		User savedUser = userRepository.save(user);

		mockMvc.perform(post("/user/auth/login")
				.param("email", email)
				.param("password", password))
				.andExpect(status().isOk());
	}

	@Test
	public void create() throws Exception {
		mockMvc.perform(post("/cards")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", token))
				.andExpect(status().isOk());
	}

	@Test
	public void update() throws Exception {

		mockMvc.perform(post("/cards")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
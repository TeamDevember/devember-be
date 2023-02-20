package com.gridians.gridians.card.controller;

import com.gridians.gridians.domain.card.controller.ProfileCardController;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.config.security.service.CustomUserDetailsService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileCardController.class)
public class ProfileCardControllerTest {


	//Controller API를 테스트하기 위한 객체
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileCardRepository profileCardRepository;

	//Bean으로 등록할 구현체를 넣어야 함
	@Autowired
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


	String email = "test@gmail.com";
	String password = "password12!";

	String accessToken;
	ProfileCard profileCard;


	@BeforeAll
	void signUp(){
		User user = new User();
		user.setId(UUID.randomUUID());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode("password12!"));

		JoinDto.Request joinRequest = new JoinDto.Request();
		joinRequest.setEmail(email);
		joinRequest.setPassword(passwordEncoder.encode(password));
		joinRequest.setNickname("test");

		userService.signUp(joinRequest);
	}

	@Test
	void create(){

		User findUser = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		LoginDto.Request request = new LoginDto.Request()
				.builder()
				.email(findUser.getEmail())
				.password(findUser.getPassword())
				.build();

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
		Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		LoginDto.Response login = userService.login(authentication);
		accessToken = login.getAccessToken();

		System.out.println(accessToken);
	}
//	@BeforeEach
//	public void beforeEach() {

//
//		accessToken = jwtUtils.createAccessToken((JwtUserDetails)customUserDetailsService.loadUserByUsername(savedUser.getEmail()));
//	}
//
//
//	@Test
//	@DisplayName("카드 생성")
//	void createProfileCard() throws Exception {
//
////		User user = userRepository.findByEmail("dlwodud821@gmail.com")
////				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
//
////		// 실제로 리턴을 할 수 없어서 아래처럼 리턴을 한다고 가정
////		given(profileCardService.createProfileCard("dlwodud821@gmail.com"))
////				.willReturn(new ProfileCard().builder().user(user).build());
//
//		mockMvc.perform(post("/cards").with(csrf())
//						.header("Authorization", "Bearer " + accessToken))
//				.andExpect(status().isOk())
//				.andDo(print());
//
//	}


}

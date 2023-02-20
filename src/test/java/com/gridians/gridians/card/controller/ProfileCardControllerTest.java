//package com.gridians.gridians.card.controller;
//
//import com.gridians.gridians.domain.card.controller.ProfileCardController;
//import com.gridians.gridians.domain.card.entity.ProfileCard;
//import com.gridians.gridians.domain.card.service.ProfileCardService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.BDDMockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//
//@WebMvcTest(ProfileCardController.class)
//public class ProfileCardControllerTest {
//
//
//	//Controller API를 테스트하기 위한 객체
//	@Autowired
//	private MockMvc mockMvc;
//
//	//Bean으로 등록할 구현체를 넣어야 함
//	@MockBean
//	ProfileCardService profileCardService;
//
//
//	@BeforeEach
//	void getToken(){
//		String clientId = "foo";
//		String clientSecret = "bar";
//		String username = "dlwodud821@gmail.com";
//		String password = "password12";
//
//		ResultActions perform = this.mockMvc.perform(post("/user/auth/login")
//				.with(httpBasic)
//				.param("username", username)
//				.param("password", password));
//	}
//
//	@Test
//	@DisplayName("카드 생성")
//	void createProfileCard() throws Exception {
//		given(profileCardService.createProfileCard("dlwodud821@gmail.com")).willReturn(new ProfileCard());
//		mockMvc.perform(post("/cards"))
//				.andExpect(status().isOk())
//				.andDo(print());
//
//		verify(profileCardService.createProfileCard("dlwodud821@gmail.com"));
//
//	}
//
//
//}

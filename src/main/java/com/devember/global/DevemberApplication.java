package com.devember.global;

import com.devember.devember.user.controller.UserController;
import com.devember.devember.user.dto.JoinDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DevemberApplication {

	@Autowired
	UserController userController;
	public static void main(String[] args) {
		SpringApplication.run(DevemberApplication.class, args);
	}

	@PostConstruct
	public void postConstruct() {
		JoinDto.Request joinDto = JoinDto.Request.builder()
				.email("email@email.com")
				.password("password")
				.nickname("nickname")
				.name("name")
				.build();
		userController.signUp(joinDto);
	}
}

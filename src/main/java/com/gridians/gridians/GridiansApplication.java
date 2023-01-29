package com.gridians.gridians;

import com.gridians.gridians.domain.user.controller.UserController;
import com.gridians.gridians.domain.user.dto.JoinDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class GridiansApplication {

	@Autowired
	UserController userController;
	public static void main(String[] args) {
		SpringApplication.run(GridiansApplication.class, args);
	}

	@PostConstruct
	public void postConstruct() {
		JoinDto.Request joinDto = JoinDto.Request.builder()
				.email("email@email.com")
				.password("password")
				.nickname("nickname")
				.build();
		userController.signUp(joinDto);
	}
}

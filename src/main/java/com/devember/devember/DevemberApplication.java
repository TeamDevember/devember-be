package com.devember.devember;

import com.devember.devember.card.entity.ProfileCard;
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
                .build();
        JoinDto.Request joinDto2 = JoinDto.Request.builder()
                .email("test@email.com")
                .password("password")
                .nickname("nickname2")
                .build();
        userController.signUp(joinDto);
		userController.signUp(joinDto2);
    }
}

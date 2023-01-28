package com.gridianz.girdianz;

import com.gridianz.girdianz.domain.user.controller.UserController;
import com.gridianz.girdianz.domain.user.dto.JoinDto;
import com.gridianz.girdianz.domain.user.entity.Role;
import com.gridianz.girdianz.domain.user.entity.User;
import com.gridianz.girdianz.domain.user.repository.UserRepository;
import com.gridianz.girdianz.domain.user.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DevemberApplication {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    public static void main(String[] args) {
        SpringApplication.run(DevemberApplication.class, args);
    }

    @PostConstruct
    @Transactional
    public void postConstruct() {
        User user = User.builder()
                .email("email@email.com")
                .password(passwordEncoder.encode("password12!"))
                .nickname("nickname")
                .role(Role.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
    }
}

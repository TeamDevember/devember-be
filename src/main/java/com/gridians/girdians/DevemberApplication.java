package com.gridians.girdians;

import com.gridians.girdians.domain.user.entity.Role;
import com.gridians.girdians.domain.user.entity.User;
import com.gridians.girdians.domain.user.repository.UserRepository;
import com.gridians.girdians.domain.user.type.UserStatus;
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

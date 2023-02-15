package com.gridians.gridians;

import com.gridians.gridians.domain.card.controller.ProfileCardController;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.user.controller.UserController;
import com.gridians.gridians.domain.user.entity.Github;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class GridiansApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    UserController userController;
    @Autowired
    ProfileCardController profileCardController;

    public static void main(String[] args) {
        SpringApplication.run(GridiansApplication.class, args);
    }

//    @PostConstruct
//    @Transactional
//    public void postConstruct() {
//        userRepository.deleteAll();
//        User user1 = User.builder()
//                .email("email@email.com")
//                .password(passwordEncoder.encode("password12!"))
//                .nickname("nickname")
//                .role(Role.USER)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//        User user2 = User.builder()
//                .email("test@email.com")
//                .password(passwordEncoder.encode("password12!"))
//                .nickname("nickname2")
//                .role(Role.USER)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//
//        User user3 = User.builder()
//                .email("qwejklasd@naver.com")
//                .password(passwordEncoder.encode("password12!"))
//                .nickname("nickname2")
//                .role(Role.USER)
//                .userStatus(UserStatus.ACTIVE)
//                .build();
//
//        User saveUser = userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//
//        userController.dummy();
//        profileCardController.dummy();
//    }
}

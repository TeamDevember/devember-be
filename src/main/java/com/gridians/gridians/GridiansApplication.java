package com.gridians.gridians;

import com.gridians.gridians.domain.card.controller.ProfileCardController;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class GridiansApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileCardRepository profileCardRepository;

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
//        for (int i = 0; i <= 99; i++) {
//            User user = User.builder().password("test1234").build();
//
//            user.setNickname("test" + i);
//            user.setEmail("test" + i + "@test.com");
//            userRepository.save(user);
//        }
//        List<User> all = userRepository.findAll();
//
//        for (User user : all) {
//            if (user.getEmail().equals("email@email.com")) {
//                continue;
//            }
//            ProfileCard pc = ProfileCard.builder().build();
//            pc.setUser(user);
//            profileCardRepository.save(pc);
//        }
//    }
}

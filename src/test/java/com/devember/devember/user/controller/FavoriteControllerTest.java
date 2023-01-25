package com.devember.devember.user.controller;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.config.security.service.CustomUserDetailsService;
import com.devember.devember.config.security.userdetail.JwtUserDetails;
import com.devember.devember.user.dto.FavoriteDto;
import com.devember.devember.user.dto.JoinDto;
import com.devember.devember.user.entity.Favorite;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.repository.FavoriteRepository;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.service.UserService;
import com.devember.devember.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class FavoriteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ProfileCardRepository profileCardRepository;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    ObjectMapper objectMapper;

    String accessToken;

    String email1 = "email@email.com";
    String email2 = "test@email.com";

    ProfileCard profileCard;


    @BeforeEach
    public void beforeEach() {
        User user2 = userRepository.findByEmail(email2).get();

        profileCard = ProfileCard.builder().id(1L).user(user2).build();
        profileCardRepository.save(profileCard);
        user2.setProfileCard(profileCard);

        accessToken = jwtUtils.createAccessToken((JwtUserDetails) customUserDetailsService.loadUserByUsername(email1));
    }

    @Test
    @DisplayName("즐겨찾기 추가")
    public void addFavorite() throws Exception {
        User user2 = userRepository.findByEmail(email2).get();
        FavoriteDto.Request content = FavoriteDto.Request.builder().email(email2).build();

        mvc.perform(post("/fav")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().isOk())
        ;

        User savedUser = userRepository.findByEmail(email1).get();
        assertThat(savedUser.getFavorites().size()).isEqualTo(1);

        Favorite savedFavor = savedUser.getFavorites().stream().findFirst().get();
        assertThat(savedFavor.getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("즐겨찾기 삭제")
    public void deleteFavorite() throws Exception {
        userService.addFavorite(email1, email2);
        FavoriteDto.Request content = FavoriteDto.Request.builder().email(email2).build();

        mvc.perform(delete("/fav")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(content))
                )
                .andExpect(status().isOk())
        ;

        User findUser = userRepository.findByEmail(email1).get();
        assertThat(findUser.getFavorites().size()).isEqualTo(0);
    }
}
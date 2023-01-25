package com.devember.devember.user.service;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.user.entity.Favorite;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.repository.FavoriteRepository;
import com.devember.devember.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    ProfileCardRepository profileCardRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    FavoriteRepository favoriteRepository;

    @Test
    @DisplayName("즐겨찾기 등록")
    public void addFavorite() {
        User user = User.builder().email("bell@email.com").password("password").build();
        User user2 = User.builder().email("kim@email.com").password("password").nickname("nickname").build();

        ProfileCard profileCard = ProfileCard.builder().id(1L).user(user2).build();

        given(profileCardRepository.findByUser(any())).willReturn(Optional.of(profileCard));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));

        userService.addFavorite(user.getEmail(), user2.getEmail());

        User savedUser = userRepository.findByEmail(user.getEmail()).get();
        assertThat(savedUser.getFavorites().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("즐겨찾기 삭제")
    public void deleteFavorite() {
        User user = User.builder().email("bell@email.com").password("password").build();
        User user2 = User.builder().email("kim@email.com").password("password").nickname("nickname").build();

        Favorite favorite = Favorite.builder().id(1L).user(user2).build();

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(favoriteRepository.findByUser(any())).willReturn(Optional.of(favorite));

        userService.deleteFavorite(user.getEmail(), user2.getNickname());

        Mockito.verify(favoriteRepository, times(1)).deleteById(favorite.getId());

        User findUser = userRepository.findByEmail(user.getEmail()).get();
        assertThat(findUser.getFavorites().size()).isEqualTo(0);
    }
}
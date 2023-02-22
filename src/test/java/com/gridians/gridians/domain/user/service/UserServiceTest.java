package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.Field;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.DuplicateEmailException;
import com.gridians.gridians.domain.user.exception.DuplicateNicknameException;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.TokenRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.MailComponent;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MailComponent mailComponent;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    TokenRepository tokenRepository;
    @Mock
    GithubService githubService;
    @Mock
    UserDto.UpdateRequest updateRequest;

    UUID uuid;
    User verifyUser;
    User notVerifyUser;
    User deleteUser;

    @BeforeEach
    public void beforeEach() {
        uuid = UUID.randomUUID();
        verifyUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .password("encodedPassword")
                .nickname("nickname")
                .role(Role.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        notVerifyUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .password("encodedPassword")
                .nickname("nickname")
                .role(Role.ANONYMOUS)
                .userStatus(UserStatus.UNACTIVE)
                .build();

        deleteUser = User.builder()
                .id(uuid)
                .email("email@email.com")
                .password("encodedPassword")
                .nickname("nickname")
                .role(Role.USER)
                .userStatus(UserStatus.DELETED)
                .build();

        ReflectionTestUtils.setField(userService, "mailComponent", mailComponent);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "jwtUtils", jwtUtils);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signUpTest() {
        JoinDto.Request request = JoinDto.Request.builder()
                .email("email@email.com")
                .password("password12!")
                .nickname("nickname")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByNickname(verifyUser.getNickname())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(notVerifyUser);

        User savedUser = userService.signUp(request);

        assertThat(savedUser.getId()).isEqualTo(notVerifyUser.getId());
        assertThat(savedUser.getEmail()).isEqualTo(notVerifyUser.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(passwordEncoder.encode(notVerifyUser.getPassword()));
        assertThat(savedUser.getUserStatus()).isEqualTo(UserStatus.UNACTIVE);
        assertThat(savedUser.getRole()).isEqualTo(Role.ANONYMOUS);
        assertThat(savedUser.getNickname()).isEqualTo(notVerifyUser.getNickname());
    }

    @Test
    @DisplayName("회원가입 이메일 중복 테스트")
    public void signUpEmailDuplicateFailTest() {
        JoinDto.Request request = JoinDto.Request.builder()
                .email("email@email.com")
                .password("password12!")
                .nickname("nickname")
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));

        assertThrows(DuplicateEmailException.class, () -> userService.signUp(request));
    }

    @Test
    @DisplayName("회원가입 닉네임 중복 테스트")
    public void signUpNicknameDuplicateTest() {
        JoinDto.Request request = JoinDto.Request.builder()
                .email("email@email.com")
                .password("password12!")
                .nickname("nickname")
                .build();

        when(userRepository.existsByNickname(verifyUser.getNickname())).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> userService.signUp(request));
    }

    @Test
    @DisplayName("이메일 인증 테스트")
    public void emailAuthTest() {
        when(userRepository.findById(notVerifyUser.getId())).thenReturn(Optional.of(notVerifyUser));
        when(userRepository.save(any())).thenReturn(notVerifyUser);

        JoinDto.Response savedUser = userService.joinAuth(uuid.toString());

        assertThat(savedUser.getNickname()).isEqualTo(verifyUser.getNickname());
    }

    @Test
    @DisplayName("유저 확인 테스트")
    public void verifyUserTest() {
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userService.verifyUser(verifyUser.getEmail(), verifyUser.getPassword());
    }

    @Test
    @DisplayName("유저 확인 테스트 실패 - 패스워드 오류")
    public void verifyUserTestFailPasswordNotMatch() {
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserException userException = assertThrows(UserException.class, () -> userService.verifyUser(verifyUser.getEmail(), verifyUser.getPassword()));
        assertThat(userException.getMessage()).isEqualTo(ErrorCode.WRONG_USER_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("유저 확인 테스트 실패 - 비활성 유저")
    public void verifyUserTestFailUnActiveUserTest() {
        when(userRepository.findByEmail(notVerifyUser.getEmail())).thenReturn(Optional.of(notVerifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UserException userException = assertThrows(UserException.class, () -> userService.verifyUser(notVerifyUser.getEmail(), notVerifyUser.getPassword()));
        assertThat(userException.getMessage()).isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED.getMessage());
    }

    @Test
    @DisplayName("유저 확인 테스트 실패 - 탈퇴한 유저")
    public void verifyUserTestFailDeleteUserTest() {
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(deleteUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UserException userException = assertThrows(UserException.class, () -> userService.verifyUser(deleteUser.getEmail(), deleteUser.getPassword()));
        assertThat(userException.getMessage()).isEqualTo(ErrorCode.DELETE_USER_ACCESS.getMessage());
    }

    @Test
    @DisplayName("로그인 테스트")
    public void signInTest() {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        JwtUserDetails jwtUserDetails = JwtUserDetails.create(verifyUser);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(jwtUserDetails);
        when(userService.createAccessToken(authentication)).thenReturn(accessToken);
        when(userService.createRefreshToken(authentication)).thenReturn(refreshToken);
        doNothing().when(tokenRepository).save(any(), any(), anyInt());

        LoginDto.Response login = userService.login(authentication);
        assertThat(login.getAccessToken()).isEqualTo(accessToken);
        assertThat(login.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("소셜 로그인 - 회원가입")
    public void socialLoginSignUpTest() {
        JoinDto.Request requestDto = JoinDto.Request.builder()
                .email("email@email.com")
                .password("password12!")
                .nickname("nickname")
                .githubNumberId(1L)
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(notVerifyUser);

        userService.signUp(requestDto);
    }

    @Test
    @DisplayName("소셜 로그인 테스트")
    public void socialLoginTest() throws Exception {
        JoinDto.Request request = JoinDto.Request.builder()
                .email("email@email.com")
                .password("password12!")
                .nickname("nickname")
                .githubNumberId(1L)
                .build();

        String token = "1";

        Authentication authentication = mock(Authentication.class);

        when(githubService.githubRequest(anyString())).thenReturn("1");
        when(userRepository.findByGithubNumberId(any())).thenReturn(Optional.of(verifyUser));
        when(jwtUtils.getAuthenticationByEmail(anyString())).thenReturn(authentication);

        Authentication savedAuthentication = userService.socialLogin(token);

        assertThat(authentication).isEqualTo(savedAuthentication);
    }

    @Test
    @DisplayName("유저 닉네임 업데이트 테스트")
    public void updateUserNickNameTest() {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("nickname2")
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));

        userService.updateUser(verifyUser.getEmail(), requestDto);

        assertThat(verifyUser.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    @DisplayName("유저 패스워드 업데이트 테스트")
    public void updateUserPasswordTest() {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("nickname")
                .password("password12!")
                .updatePassword("updatePassword12!")
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.updateUser("email@email.com", requestDto);

        assertThat(verifyUser.getEmail()).isEqualTo("email@email.com");
        assertThat(verifyUser.getNickname()).isEqualTo(requestDto.getNickname());
        assertThat(verifyUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("유저 닉네임, 패스워드 업데이트 테스트")
    public void updateUserNicknameAndPasswordTest() {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("nickname1")
                .password("password12!")
                .updatePassword("updatePassword12!")
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("updatedPassword");

        userService.updateUser("email@email.com", requestDto);

        assertThat(verifyUser.getEmail()).isEqualTo("email@email.com");
        assertThat(verifyUser.getPassword()).isEqualTo("updatedPassword");
        assertThat(verifyUser.getPassword()).isNotEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("유저 패스워드 업데이트 실패 테스트 - 패스워드 불일치")
    public void updateUserPasswordTestFail() {
        UserDto.UpdateRequest requestDto = UserDto.UpdateRequest.builder()
                .nickname("nickname1")
                .password("password12!")
                .updatePassword("updatePassword12!")
                .build();

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserException userException = assertThrows(UserException.class, () -> userService.updateUser("email@email.com", requestDto));

        assertThat(userException.getMessage()).isEqualTo(ErrorCode.WRONG_USER_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("유저 이메일 업데이트 테스트")
    public void updateUserEmailTest() {
        String updateEmail = "test@email.com";

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(userRepository.findByEmail(updateEmail)).thenReturn(Optional.empty());

        userService.updateEmail(verifyUser.getEmail(), updateEmail);

        assertThat(verifyUser.getEmail()).isEqualTo(updateEmail);
    }

    @Test
    @DisplayName("비밀번호 초기화 테스트")
    public void initUserPasswordTest() {
        String initPassword = "initPassword";

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.encode(anyString())).thenReturn(initPassword);

        userService.findPassword(verifyUser.getEmail());

        assertThat(verifyUser.getPassword()).isEqualTo(initPassword);
    }

    @Test
    @DisplayName("유저 탈퇴 테스트")
    public void deleteUserTest() {
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userService.deleteUser(verifyUser.getEmail(), verifyUser.getPassword());

        assertThat(verifyUser.getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("유저 탈퇴 테스트 실패 - 패스워드 불일치")
    public void deleteUserTestFail() {
        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserException userException = assertThrows(UserException.class, () -> userService.deleteUser(verifyUser.getEmail(), verifyUser.getPassword()));

        assertThat(userException.getMessage()).isEqualTo(ErrorCode.WRONG_USER_PASSWORD.getMessage());
        assertThat(verifyUser.getUserStatus()).isNotEqualTo(UserStatus.DELETED);
        assertThat(verifyUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    public void reissueAccessTokenTest() {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Authentication authentication = mock(Authentication.class);

        when(tokenRepository.hasKeyToken(refreshToken)).thenReturn(true);
        when(jwtUtils.getAuthenticationByToken(anyString())).thenReturn(authentication);
        when(jwtUtils.createAccessToken(authentication)).thenReturn(accessToken);

        String reissueToken = userService.issueAccessToken(refreshToken);

        assertThat(reissueToken).isEqualTo(accessToken);
    }


    @Test
    @DisplayName("즐겨찾기 리스트 반환 테스트")
    public void getFavorites() {
        List<ProfileCard> dummyProfileCardList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ProfileCard dummyProfileCard = ProfileCard.builder()
                    .id(Long.valueOf(i))
                    .build();

            dummyProfileCardList.add(dummyProfileCard);
        }

        for (int i = 0; i < 10; i++) {
            UUID uuid = UUID.randomUUID();
            User dummyUser = User.builder()
                    .id(uuid)
                    .email("email" + i + "@email.com")
                    .nickname("nickname" + i)
                    .password("pass")
                    .profileCard(dummyProfileCardList.get(i))
                    .build();
            dummyProfileCardList.get(i).setUser(dummyUser);

            Favorite favorite = Favorite.builder()
                    .id(Long.valueOf(i))
                    .user(verifyUser)
                    .favoriteUser(dummyUser)
                    .build();

            verifyUser.addFavorite(favorite);
        }

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));

        HashSet<ProfileCardDto.SimpleResponse> simpleResponses = userService.favoriteList(verifyUser.getEmail());

        assertThat(simpleResponses.size()).isEqualTo(10);

        Set<Long> profileCardIdSet = simpleResponses.stream()
                .map(simpleResponse -> simpleResponse.getProfileCardId())
                .collect(Collectors.toSet());

        Set<String> profileCardUserNicknameSet = simpleResponses.stream()
                .map(simpleResponse -> simpleResponse.getNickname())
                .collect(Collectors.toSet());

        for (int i = 0; i < 10; i++) {
            assertThat(profileCardIdSet).contains(Long.valueOf(i));
            assertThat(profileCardUserNicknameSet).contains("nickname" + i);
        }
    }


    @Test
    @DisplayName("즐겨찾기 등록")
    public void addFavorite() {
        ProfileCard profileCard = ProfileCard.builder()
                .id(1L)
                .build();

        User user = User.builder()
                .email("test@test.com")
                .nickname("test")
                .password("test")
                .profileCard(profileCard)
                .build();

        profileCard.setUser(user);

        Favorite favorite = Favorite
                .builder()
                .user(verifyUser)
                .favoriteUser(user)
                .build();

        when(profileCardRepository.findById(profileCard.getId())).thenReturn(Optional.of(profileCard));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(verifyUser));
        when(userRepository.findByProfileCard_Id(profileCard.getId())).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserAndFavoriteUser(verifyUser, user)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        HashSet<ProfileCardDto.SimpleResponse> simpleResponses = userService.addFavorite(verifyUser.getEmail(), profileCard.getId());

        assertThat(simpleResponses.size()).isEqualTo(1);
    }
    @Test
    @DisplayName("즐겨찾기 삭제")
    public void deleteFavorite() {
        ProfileCard profileCard = ProfileCard.builder()
                .id(1L)
                .build();

        User user = User.builder()
                .email("test@test.com")
                .nickname("test")
                .password("test")
                .profileCard(profileCard)
                .build();

        profileCard.setUser(user);

        Favorite favorite = Favorite.builder()
                .id(1L)
                .user(verifyUser)
                .favoriteUser(user)
                .build();
        verifyUser.addFavorite(favorite);

        when(userRepository.findByEmail(verifyUser.getEmail())).thenReturn(Optional.of(verifyUser));
        when(profileCardRepository.findById(profileCard.getId())).thenReturn(Optional.of(profileCard));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserAndFavoriteUser(verifyUser, user)).thenReturn(Optional.of(favorite));

        userService.deleteFavorite(verifyUser.getEmail(), profileCard.getId());

        verify(favoriteRepository, times(1)).delete(favorite);
        assertThat(verifyUser.getFavorites().size()).isEqualTo(0);
    }
}
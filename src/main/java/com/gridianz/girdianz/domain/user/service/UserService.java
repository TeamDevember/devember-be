package com.gridianz.girdianz.domain.user.service;

import com.gridianz.girdianz.domain.card.entity.ProfileCard;
import com.gridianz.girdianz.domain.card.repository.ProfileCardRepository;
import com.gridianz.girdianz.domain.user.dto.JoinDto;
import com.gridianz.girdianz.domain.user.dto.UserDto;
import com.gridianz.girdianz.domain.user.entity.Favorite;
import com.gridianz.girdianz.domain.user.entity.Role;
import com.gridianz.girdianz.domain.user.entity.User;
import com.gridianz.girdianz.domain.user.exception.*;
import com.gridianz.girdianz.domain.user.repository.FavoriteRepository;
import com.gridianz.girdianz.domain.user.repository.TokenRepository;
import com.gridianz.girdianz.domain.user.repository.UserRepository;
import com.gridianz.girdianz.domain.user.type.MailMessage;
import com.gridianz.girdianz.domain.user.type.UserErrorCode;
import com.gridianz.girdianz.domain.user.type.UserStatus;
import com.gridianz.girdianz.global.config.MailComponent;
import com.gridianz.girdianz.global.error.exception.EntityNotFoundException;
import com.gridianz.girdianz.global.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileCardRepository profileCardRepository;
    private final FavoriteRepository favoriteRepository;
    private final MailComponent mailComponent;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;
    private final SocialRequest socialRequest;

    @Transactional
    public User signUp(JoinDto.Request request) throws RuntimeException {
        User user = User.from(request);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        User savedUser;

        if (optionalUser.isPresent()) { //중복 이메일
            savedUser = optionalUser.get();
            throw new DuplicateEmailException(savedUser.getEmail());
        } else {
            user.setUserStatus(UserStatus.UNACTIVE);
            user.setRole(Role.ANONYMOUS);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setNickname(user.getNickname());
            savedUser = userRepository.save(user);
            mailComponent.sendMail(user.getEmail(), MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setContentMessage(savedUser.getId()));

            return savedUser;
        }
    }

    @Transactional
    public UserDto.JoinResponse joinAuth(String id) {
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.setRole(Role.USER);
        user.setUserStatus(UserStatus.ACTIVE);

        return UserDto.JoinResponse.from(userRepository.save(user));
    }

    public void verifyUser(String email, String password) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("password not match");
        }
        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
        }
        if(user.getUserStatus() == UserStatus.DELETED) {
            throw new UserDeleteException("deleted user");
        }
    }

    public String issueAccessToken(String refreshToken) {
        String issuedAccessToken = "";
        try {
            if (StringUtils.hasText(refreshToken) && jwtUtils.validateToken(refreshToken)) {
                Authentication authentication = jwtUtils.getAuthenticationByToken(refreshToken);
                issuedAccessToken = jwtUtils.createAccessToken(authentication);
            }
        } catch (Exception e) {
            throw new JwtException("Refresh Token Exception");
        }

        return issuedAccessToken;
    }

    public String createAccessToken(Authentication authentication) {
        return jwtUtils.createAccessToken(authentication);
    }

    public String createRefreshToken(Authentication authentication) {
        return jwtUtils.createRefreshToken(authentication);
    }

    public void logout(String accessToken, String refreshToken) {
        String email = jwtUtils.getUserEmailFromToken(refreshToken);
        tokenRepository.saveBlackList(accessToken, email, jwtUtils.ACCESS_TOKEN_EXPIRE_TIME.intValue());
        tokenRepository.saveBlackList(refreshToken, email, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());
    }

    public boolean checkUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void addFavorite(String email, String favorUserEmail) {
        User user = getUserByEmail(email);
        User favorUser = getUserByEmail(favorUserEmail);

        ProfileCard profileCard = profileCardRepository.findByUser(favorUser)
                .orElseThrow(() -> new RuntimeException("profile card not found"));

//		if(user.getProfileCard() == null) {
//			throw new RuntimeException("no pofilecard");
//		}

        Favorite favorite = Favorite.builder()
                .user(favorUser)
                .build();
        favoriteRepository.save(favorite);
        user.addFavorite(favorite);
    }

    @Transactional
    public void deleteFavorite(String email, String favorUserEmail) {
        User user = getUserByEmail(email);
        User favorUser = getUserByEmail(favorUserEmail);

        Favorite favorite = favoriteRepository.findByUser(favorUser)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        user.deleteFavorite(favorite);
        favoriteRepository.deleteById(favorite.getId());
    }


    @Transactional
    public Authentication socialLogin(String token) throws Exception {
        Long githubId = Long.valueOf(socialRequest.githubRequest(token));

        User user = userRepository.findByGithubNumberId(githubId)
                .orElseThrow(() -> new GithubIdNotFoundException("user not found", githubId.toString()));

        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
        }

        return jwtUtils.getAuthenticationByEmail(user.getEmail());
    }

    public void verifyUserPassword(String email, String password) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("password not match");
        }
    }

    @Transactional
    public void findPassword(String email) {
        String uuid = UUID.randomUUID().toString();
        updatePassword(email, uuid);

        mailComponent.sendPasswordMail(email, MailMessage.EMAIL_PASSWORD_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
    }

    @Transactional
    public void updatePassword(String email, String password) {
        User user = getUserByEmail(email);

        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void updateNickname(String userEmail, String nickname) {
        User user = getUserByEmail(userEmail);
        user.setNickname(nickname);
    }

    @Transactional
    public void deleteUser(String userEmail) {
        User user = getUserByEmail(userEmail);
        user.setUserStatus(UserStatus.DELETED);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email + "not found"));
    }

    public void updateEmail(String userEmail, String updateEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(userEmail + "not found"));
        user.setEmail(updateEmail);
    }

    public void sendUpdateEmail(String userEmail, String updateEmail) {
        mailComponent.sendUpdateEmail(updateEmail, MailMessage.EMAIL_EMAIL_UPDATE, updateEmail);
    }
}

package com.devember.devember.user.service;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.config.MailComponent;
import com.devember.devember.config.error.exception.EntityNotFoundException;
import com.devember.devember.user.dto.JoinDto;
import com.devember.devember.user.dto.UserDto;
import com.devember.devember.user.entity.Favorite;
import com.devember.devember.user.entity.Role;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.exception.DuplicateEmailException;
import com.devember.devember.user.exception.EmailNotVerifiedException;
import com.devember.devember.user.exception.PasswordNotMatchException;
import com.devember.devember.user.exception.UserException;
import com.devember.devember.user.repository.FavoriteRepository;
import com.devember.devember.user.repository.TokenRepository;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.type.MailMessage;
import com.devember.devember.user.type.UserErrorCode;
import com.devember.devember.user.type.UserStatus;
import com.devember.devember.utils.JwtUtils;
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
        user.setUserStatus(UserStatus.ACTIVE);

        return UserDto.JoinResponse.from(userRepository.save(user));
    }

    public void verifyUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email + "not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("password not match");
        }
        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        User favorUser = userRepository.findByEmail(favorUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User favorUser = userRepository.findByEmail(favorUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Favorite favorite = favoriteRepository.findByUser(favorUser)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        user.deleteFavorite(favorite);
        favoriteRepository.deleteById(favorite.getId());
    }


    @Transactional
    public Authentication socialLogin(String token) throws Exception {
        Long githubId = Long.valueOf(socialRequest.githubRequest(token));

        User user = userRepository.findByGithubNumberId(githubId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        if (user.getUserStatus() == UserStatus.UNACTIVE) {
            throw new EmailNotVerifiedException("email not verified");
        }

        return jwtUtils.getAuthenticationByEmail(user.getEmail());
    }

    public void verifyUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("not found user"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("password not match");
        }
    }

    @Transactional
    public void findPassword(String email) {
        String uuid = UUID.randomUUID().toString();
        updatePassword(email, uuid);

        mailComponent.sendMail(email, MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
    }

    @Transactional
    public void updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("not found user"));

        user.setPassword(passwordEncoder.encode(password));
    }
}

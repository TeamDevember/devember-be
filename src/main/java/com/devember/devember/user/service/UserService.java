package com.devember.devember.user.service;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.config.MailComponent;
import com.devember.devember.user.dto.JoinDto;
import com.devember.devember.user.dto.UserDto;
import com.devember.devember.user.entity.CreateType;
import com.devember.devember.user.entity.Favorite;
import com.devember.devember.user.entity.Role;
import com.devember.devember.user.entity.User;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
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
	private final AuthenticationManager authenticationManager;
//	private final SocialRequest socialRequest;

	@Transactional
	public User signUp(JoinDto.Request request) {
		User user = User.from(request);

		Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
		User savedUser;

		if(optionalUser.isPresent()) {
			savedUser = optionalUser.get();
			if(savedUser.getCreateType() != CreateType.EMAIL){
				savedUser.setUserStatus(UserStatus.ACTIVE);
				savedUser.setName(user.getName());
			}
			else if(savedUser.getCreateType() == CreateType.EMAIL) {
				return null;
			}
		}
		else {
			user.setUserStatus(UserStatus.UNACTIVE);
			user.setCreateType(CreateType.EMAIL);
			user.setRole(Role.USER);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setNickname(user.getNickname());
			savedUser = userRepository.save(user);
			mailComponent.sendMail(user.getEmail(), MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setContentMessage(savedUser.getId()));
		}

		return savedUser;
	}

	@Transactional
	public UserDto.JoinResponse joinAuth(String id){
		User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		user.setUserStatus(UserStatus.ACTIVE);

		return UserDto.JoinResponse.from(userRepository.save(user));
	}

	public int verifyUser(String email, String password) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if(optionalUser.isEmpty()){
			return 1;
		}
		User user = optionalUser.get();

		if(!passwordEncoder.matches(password, user.getPassword())){
			return 2;
		}

		return 3;
	}

	public String issueAccessToken(String refreshToken) {
		String issuedAccessToken = "";
		try {
			if(StringUtils.hasText(refreshToken) && jwtUtils.validateToken(refreshToken)){
				Authentication authentication = jwtUtils.getAuthenticationByToken(refreshToken);
				issuedAccessToken = jwtUtils.createAccessToken(authentication);
				String email = jwtUtils.getUserEmailFromToken(issuedAccessToken);
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

	public boolean checkUser(String email){
		if(userRepository.findByEmail(email).isPresent()){
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


//
//	@Transactional
//	public Authentication socialLogin(String token, String status) throws Exception {
//		String email = "";
//		if(CreateType.KAKAO.getName().equals(status)) {
//			email = socialRequest.kakaoRequest(token);
//		}
//		else if(CreateType.GOOGLE.getName().equals(status)){
//			email = socialRequest.googleRequest(token);
//		}
//		else if(CreateType.GITHUB.getName().equals(status)){
//			email = socialRequest.githubRequest(token);
//		}
//		else {
//			throw new RuntimeException("Not Found Login Type");
//		}
//
//		log.info("email = {}", email);
//		Optional<User> optionalUser = userRepository.findByEmail(email);
//
//		if(optionalUser.isEmpty()){
//			userRepository.save(User.builder()
//					.email(email)
//					.role(Role.ANONYMOUS)
//					.createType(CreateType.valueOf(status))
//					.userStatus(CardStatus.UNACTIVE)
//					.build()
//			);
//		}
//
//		Authentication authentication = jwtUtils.getAuthenticationByEmail(email);
//
//		return authentication;
//	}
}

package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.*;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.TokenRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.MailMessage;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.config.MailComponent;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.error.exception.CustomJwtException;
import com.gridians.gridians.global.error.exception.EntityNotFoundException;
import com.gridians.gridians.global.error.exception.ErrorCode;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final ImageService imageService;
	private final UserRepository userRepository;
	private final ProfileCardRepository profileCardRepository;
	private final FavoriteRepository favoriteRepository;
	private final MailComponent mailComponent;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final TokenRepository tokenRepository;
	private final GithubService githubService;
	private final ProfileCardService profileCardService;

	@Value("${server.host.api}")
	private String serverApi;

	@Transactional
	public User signUp(JoinDto.Request request) throws Exception {
		User user = User.from(request);

		Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

		if (optionalUser.isPresent()) { //중복 이메일
			throw new DuplicateEmailException(optionalUser.get().getEmail());
		}

		if (userRepository.existsByNickname(user.getNickname())) {
			throw new DuplicateNicknameException(user.getNickname());
		} else {
			user.setUserStatus(UserStatus.UNACTIVE);
			user.setRole(Role.ANONYMOUS);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setNickname(user.getNickname());
			User savedUser = userRepository.save(user);
			if (request.getGithubNumberId() != null) {
				user.setGithubNumberId(request.getGithubNumberId());
				profileCardService.saveGithub(user.getEmail(), request.getGithubNumberId().toString());
			}
			mailComponent.sendMail(user.getEmail(), MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setContentMessage(savedUser.getId()));

			return savedUser;
		}
	}

	@Transactional
	public JoinDto.Response joinAuth(String id) {
		User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		user.setRole(Role.USER);
		user.setUserStatus(UserStatus.ACTIVE);

		return JoinDto.Response.from(userRepository.save(user));
	}

	public void verifyUser(String email, String password) {
		User user = getUserByEmail(email);

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new PasswordNotMatchException("password not match");
		}
		if (user.getUserStatus() == UserStatus.UNACTIVE) {
			throw new EmailNotVerifiedException("email not verified");
		}
		if (user.getUserStatus() == UserStatus.DELETED) {
			throw new UserDeleteException("deleted user");
		}
	}

	public LoginDto.Response login(Authentication authentication) {
		String accessToken = createAccessToken(authentication);
		String refreshToken = createRefreshToken(authentication);
		JwtUserDetails userDetails = ((JwtUserDetails) authentication.getPrincipal());

		String email = userDetails.getEmail();
//        String id = userDetails.getUserId();
		String nickname = userDetails.getUser().getNickname();
		tokenRepository.save(refreshToken, email, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());


		return LoginDto.Response.from(accessToken, refreshToken, nickname);
	}

	public String issueAccessToken(String refreshToken) {
		String issuedAccessToken = "";
		try {
			if (StringUtils.hasText(refreshToken) && tokenRepository.hasKeyToken(refreshToken)) {
				Authentication authentication = jwtUtils.getAuthenticationByToken(refreshToken);
				issuedAccessToken = jwtUtils.createAccessToken(authentication);
			}
		} catch (Exception e) {
			throw new CustomJwtException("no refresh key");
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
	public void addFavorite(String email, Long favoriteProfileCardId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		ProfileCard fvCard = profileCardRepository.findById(favoriteProfileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		User favoriteUser = userRepository.findByProfileCard_Id(fvCard.getId())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
		;

		Optional<Favorite> findFavorite = favoriteRepository.findByUserAndFavoriteUser(user, favoriteUser);
		if (findFavorite.isPresent()) {
			throw new DuplicateFavoriteUserException("Duplicated favorite user");
		}

		Favorite favorite = Favorite.builder()
				.user(user)
				.favoriteUser(favoriteUser)
				.build();

		Favorite savedFavorite = favoriteRepository.save(favorite);
		user.addFavorite(savedFavorite);
		userRepository.save(user);
	}

	@Transactional
	public void deleteFavorite(String email, Long favoriteProfileCardId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		ProfileCard fvCard = profileCardRepository.findById(favoriteProfileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		User favoriteUser = userRepository.findById(fvCard.getUser().getId())
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Favorite favorite = favoriteRepository.findByUserAndFavoriteUser(user, favoriteUser)
				.orElseThrow(() -> new UserException(ErrorCode.FAVORITE_USER_NOT_FOUND));

		favoriteRepository.delete(favorite);
		user.deleteFavorite(favorite);
	}


	@Transactional
	public Authentication socialLogin(String token) throws Exception {
		Long githubId = Long.valueOf(githubService.githubRequest(token));

		User user = userRepository.findByGithubNumberId(githubId)
				.orElseThrow(() -> new GithubIdNotFoundException("user not found", githubId.toString()));

		if (user.getUserStatus() == UserStatus.UNACTIVE) {
			throw new EmailNotVerifiedException("email not verified");
		}

		return jwtUtils.getAuthenticationByEmail(user.getEmail());
	}

	@Transactional
	public void deleteUser(String userEmail, String password) {
		User user = getUserByEmail(userEmail);
		if (!verifyPassword(password, user.getPassword())) {
			throw new PasswordNotMatchException("password not match");
		}

		user.setUserStatus(UserStatus.DELETED);
	}

	@Transactional
	public void updateEmail(String userEmail, String updateEmail) {
		User user = getUserByEmail(userEmail);
		Optional<User> findUser = userRepository.findByEmail(updateEmail);

		if (findUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATED_EMAIL);
		}

		user.setEmail(updateEmail);
	}

	@Transactional
	public UserDto.Response updateUser(String userEmail, UserDto.Request userDto) {
		User user = getUserByEmail(userEmail);

		user.setNickname(userDto.getNickname());

		if (!userDto.getPassword().isEmpty()) {
			if (verifyPassword(userDto.getPassword(), user.getPassword())) {
				user.setPassword(passwordEncoder.encode(userDto.getUpdatePassword()));
			}
		}

		return UserDto.Response.from(user);
	}

	@Transactional
	public void findPassword(String email) {
		String uuid = UUID.randomUUID().toString();
		User user = getUserByEmail(email);
		user.setPassword(passwordEncoder.encode(uuid));

		mailComponent.sendPasswordMail(email, MailMessage.EMAIL_PASSWORD_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
	}

	public void sendUpdateEmail(String userEmail, String updateEmail) {
		mailComponent.sendUpdateEmail(updateEmail, MailMessage.EMAIL_EMAIL_UPDATE, MailMessage.setEmailUpdateMessage(updateEmail));
	}


	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + "not found"));
	}


	private boolean verifyPassword(String rawPassword, String cryptPassword) {
		if (!passwordEncoder.matches(rawPassword, cryptPassword)) {
			throw new PasswordNotMatchException("password not match");
		}

		return true;
	}

	public UserDto.Response getUserInfo(String userEmail) throws IOException {
		User user = getUserByEmail(userEmail);
		UserDto.Response userInfo = UserDto.Response.from(user);
		userInfo.setProfileImage(serverApi + "/profile-image/" + userEmail);
		return userInfo;
	}

	@Transactional
	public void dummyUser() {

		for (int i = 0; i <= 99; i++) {
			User user = User.builder().password("test1234").build();

			user.setNickname("test" + i);
			user.setEmail("test" + i + "@test.com");
			userRepository.save(user);
		}
	}

	public HashSet<ProfileCardDto.SimpleResponse> favoriteList(String email) throws IOException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email));

		Set<Favorite> favorites = user.getFavorites();
		HashSet<ProfileCardDto.SimpleResponse> responseList = new HashSet<>();

		for (Favorite favorite : favorites) {
			User favorUser = favorite.getFavoriteUser();

			ProfileCardDto.SimpleResponse response =
					ProfileCardDto.SimpleResponse.from(favorUser.getProfileCard());
			response.setProfileImage(serverApi + "/profile-image/" + email);
			responseList.add(response);
		}

		return responseList;
	}
}

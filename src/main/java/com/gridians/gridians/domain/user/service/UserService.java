package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Role;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.*;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.GithubRepository;
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

import java.util.HashSet;
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
	private final GithubService githubService;
	private final GithubRepository githubRepository;


	@Value("${server.host.api}")
	private String server;

	@Value("${custom.path.github}")
	private String githubApi;

	@Value("${custom.path.profile}")
	private String profilePath;
	private String separator = "/";

	@Transactional
	public User signUp(JoinDto.Request request) {
		User user = User.from(request);

		if (request.getGithubNumberId() != null && userRepository.findByGithubNumberId(request.getGithubNumberId()).isPresent()) {
			throw new UserException(ErrorCode.DUPLICATED_GITHUB_ID);
		}

		checkDuplicateEmail(request.getEmail());
		existByNickname(user.getNickname());

		user.setNickname(user.getNickname());
		user.setUserStatus(UserStatus.UNACTIVE);
		user.setRole(Role.ANONYMOUS);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);

		mailComponent.sendMail(savedUser.getEmail(),
				MailMessage.EMAIL_AUTH_MESSAGE,
				MailMessage.setContentMessage(savedUser.getId()));
		return savedUser;
	}


	@Transactional
	public JoinDto.Response joinAuth(String id) {
		User findUser = userRepository.findById(UUID.fromString(id))
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		findUser.setRole(Role.USER);
		findUser.setUserStatus(UserStatus.ACTIVE);

		if (findUser.getGithubNumberId() != null) {
			githubService.updateGithub(findUser.getEmail(), findUser.getGithubNumberId());
		}

		return JoinDto.Response.from(userRepository.save(findUser));
	}

	public LoginDto.Response login(Authentication authentication) {
		String accessToken = createAccessToken(authentication);
		String refreshToken = createRefreshToken(authentication);
		JwtUserDetails userDetails = ((JwtUserDetails) authentication.getPrincipal());

		String email = userDetails.getEmail();
		String nickname = userDetails.getUser().getNickname();
		tokenRepository.save(refreshToken, email, jwtUtils.getRefreshTokenExpireTime().intValue());

		return LoginDto.Response.from(accessToken, refreshToken, nickname);
	}

	@Transactional
	public UserDto.DefaultResponse updateUser(String userEmail, UserDto.UpdateRequest userDto) {
		User user = findUserByEmail(userEmail);

		if (StringUtils.hasText(userDto.getPassword())) {
			verifyPassword(userDto.getPassword(), user.getPassword());
			user.setPassword(passwordEncoder.encode(userDto.getUpdatePassword()));
		}

		user.setNickname(userDto.getNickname());
		return UserDto.DefaultResponse.from(user);
	}

	@Transactional
	public void deleteUser(String userEmail, String password) {
		User user = findUserByEmail(userEmail);
		verifyPassword(password, user.getPassword());
		user.setUserStatus(UserStatus.DELETED);
	}

	@Transactional
	public void updateEmail(String userEmail, String updateEmail) {
		User user = findUserByEmail(userEmail);
		checkDuplicateEmail(updateEmail);
		user.setEmail(updateEmail);
	}

	@Transactional
	public void findPassword(String email) {
		String uuid = UUID.randomUUID().toString();
		User user = findUserByEmail(email);
		user.setPassword(passwordEncoder.encode(uuid));

		mailComponent.sendPasswordMail(email, MailMessage.EMAIL_PASSWORD_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
	}

	public void verifyUser(String email, String password) {
		User user = findUserByEmail(email);
		verifyPassword(password, user.getPassword());

		if (user.getUserStatus() == UserStatus.UNACTIVE) {
			throw new UserException(ErrorCode.EMAIL_NOT_VERIFIED);
		}
		if (user.getUserStatus() == UserStatus.DELETED) {
			throw new UserException(ErrorCode.DELETE_USER_ACCESS);
		}
	}

	public String issueAccessToken(String refreshToken) {
		String issuedAccessToken = "";
		try {
			if (StringUtils.hasText(refreshToken) && tokenRepository.hasKeyToken(refreshToken)) {
				Authentication authentication = jwtUtils.getAuthenticationByToken(refreshToken);
				issuedAccessToken = jwtUtils.createAccessToken(authentication);
			}
		} catch (Exception e) {
			throw new CustomJwtException("No refresh key");
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
		tokenRepository.saveBlackList(accessToken, email, jwtUtils.getAccessTokenExpireTime().intValue());
		tokenRepository.saveBlackList(refreshToken, email, jwtUtils.getRefreshTokenExpireTime().intValue());
	}

	public boolean checkUser(String email) {
		return userRepository.findByEmail(email).isPresent() ? true : false;
	}

	@Transactional
	public HashSet<ProfileCardDto.SimpleResponse> addFavorite(String email, Long favoriteProfileCardId) {

		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		ProfileCard findProfileCard = profileCardRepository.findById(favoriteProfileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		User findFavoriteUser = userRepository.findByProfileCard_Id(findProfileCard.getId())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		if (findUser == findFavoriteUser) {
			throw new UserException(ErrorCode.DO_NOT_ADD_YOURSELF);
		}

		Optional<Favorite> optionalFavorite = favoriteRepository.findByUserAndFavoriteUser(findUser, findFavoriteUser);

		if (optionalFavorite.isPresent()) {
			throw new DuplicateFavoriteUserException("Duplicated favorite user");
		}

		Favorite favorite = Favorite.builder()
				.user(findUser)
				.favoriteUser(findFavoriteUser)
				.build();

		Favorite savedFavorite = favoriteRepository.save(favorite);
		findUser.addFavorite(savedFavorite);
		userRepository.save(findUser);

		return getFavorites(findUser);
	}


	public HashSet<ProfileCardDto.SimpleResponse> favoriteList(String email) {
		User findUser = findUserByEmail(email);
		return getFavorites(findUser);
	}

	public HashSet<ProfileCardDto.SimpleResponse> getFavorites(User user) {
		HashSet<ProfileCardDto.SimpleResponse> responseList = new HashSet<>();

		for (Favorite favor : user.getFavorites()) {
			User favorUser = favor.getFavoriteUser();

			ProfileCardDto.SimpleResponse response =
					ProfileCardDto.SimpleResponse.from(favorUser.getProfileCard());
			response.setProfileImage(server + separator + profilePath + separator + favorUser.getEmail());
			responseList.add(response);
		}

		return responseList;
	}

	@Transactional
	public void deleteFavorite(String email, Long favoriteProfileCardId) {
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		ProfileCard findProfileCard = profileCardRepository.findById(favoriteProfileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		User findFavoriteUser = userRepository.findById(findProfileCard.getUser().getId())
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Favorite findFavorite = favoriteRepository.findByUserAndFavoriteUser(findUser, findFavoriteUser)
				.orElseThrow(() -> new UserException(ErrorCode.FAVORITE_USER_NOT_FOUND));

		favoriteRepository.delete(findFavorite);
		findUser.deleteFavorite(findFavorite);
	}

	@Transactional
	public Authentication socialLogin(String token) throws Exception {
		Long githubId = Long.valueOf(githubService.githubRequest(token));

		User findUser = userRepository.findByGithubNumberId(githubId)
				.orElseThrow(() -> new GithubIdNotFoundException("User not found", githubId.toString()));

		if (findUser.getUserStatus() == UserStatus.UNACTIVE) {
			throw new EmailNotVerifiedException("Email not verified");
		}

		return jwtUtils.getAuthenticationByEmail(findUser.getEmail());
	}

	public void sendUpdateEmail(String email, String updateEmail) {
		mailComponent.sendUpdateEmail(
				updateEmail,
				MailMessage.EMAIL_EMAIL_UPDATE,
				MailMessage.setEmailUpdateMessage(updateEmail)
		);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + "not found"));
	}

	private void checkDuplicateEmail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_EMAIL);
		}
	}

	private void verifyPassword(String rawPassword, String cryptPassword) {
		if (!passwordEncoder.matches(rawPassword, cryptPassword)) {
			throw new UserException(ErrorCode.WRONG_USER_PASSWORD);
		}

	}

	public UserDto.DefaultResponse getUserInfo(String userEmail) {
		User findUser = findUserByEmail(userEmail);
		UserDto.DefaultResponse userInfo = UserDto.DefaultResponse.from(findUser);
		userInfo.setProfileImage(
				server + separator + profilePath + separator + userEmail);
		return userInfo;
	}

	public void existByNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new DuplicateNicknameException(nickname);
		}
	}
}

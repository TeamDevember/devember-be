package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.user.dto.GithubDto;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Github;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final GithubRepository githubRepository;
	private final UserRepository userRepository;
	private final ProfileCardRepository profileCardRepository;
	private final FavoriteRepository favoriteRepository;
	private final MailComponent mailComponent;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final TokenRepository tokenRepository;
	private final GithubService githubService;


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

		Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

		if (optionalUser.isPresent()) {
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
				updateGithub(user.getEmail(), request.getGithubNumberId().toString());
			}
			mailComponent.sendMail(user.getEmail(), MailMessage.EMAIL_AUTH_MESSAGE, MailMessage.setContentMessage(savedUser.getId()));
			return savedUser;
		}
	}

	@Transactional
	public JoinDto.Response joinAuth(String id) {
		User findUser = userRepository.findById(UUID.fromString(id))
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		findUser.setRole(Role.USER);
		findUser.setUserStatus(UserStatus.ACTIVE);

		return JoinDto.Response.from(userRepository.save(findUser));
	}

	public LoginDto.Response login(Authentication authentication) {
		String accessToken = createAccessToken(authentication);
		String refreshToken = createRefreshToken(authentication);
		JwtUserDetails userDetails = ((JwtUserDetails) authentication.getPrincipal());

		String email = userDetails.getEmail();
		String nickname = userDetails.getUser().getNickname();
		tokenRepository.save(refreshToken, email, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());


		return LoginDto.Response.from(accessToken, refreshToken, nickname);
	}

	@Transactional
	public UserDto.DefaultResponse updateUser(String userEmail, UserDto.UpdateRequest userDto) {
		User user = verifyUserByEmail(userEmail);

		user.setNickname(userDto.getNickname());

		if (!userDto.getPassword().isEmpty()) {
			if (verifyPassword(userDto.getPassword(), user.getPassword())) {
				user.setPassword(passwordEncoder.encode(userDto.getUpdatePassword()));
			}
		}

		return UserDto.DefaultResponse.from(user);
	}
	
	@Transactional
	public void deleteUser(String userEmail, String password) {
		User user = verifyUserByEmail(userEmail);
		if (!verifyPassword(password, user.getPassword())) {
			throw new UserException(ErrorCode.WRONG_USER_PASSWORD);
		}

		user.setUserStatus(UserStatus.DELETED);
	}

	@Transactional
	public void updateEmail(String userEmail, String updateEmail) {
		User user = verifyUserByEmail(userEmail);
		Optional<User> findUser = userRepository.findByEmail(updateEmail);

		if (findUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATED_EMAIL);
		}

		user.setEmail(updateEmail);
	}

	@Transactional
	public void findPassword(String email) {
		String uuid = UUID.randomUUID().toString();
		User user = verifyUserByEmail(email);
		user.setPassword(passwordEncoder.encode(uuid));

		mailComponent.sendPasswordMail(email, MailMessage.EMAIL_PASSWORD_MESSAGE, MailMessage.setPasswordContentMessage(uuid));
	}

	public void verifyUser(String email, String password) {
		User user = verifyUserByEmail(email);

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new UserException(ErrorCode.WRONG_USER_PASSWORD);
		}
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
	public HashSet<ProfileCardDto.SimpleResponse> addFavorite(String email, Long favoriteProfileCardId) {
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		ProfileCard findProfileCard = profileCardRepository.findById(favoriteProfileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		User findFavoriteUser = userRepository.findByProfileCard_Id(findProfileCard.getId())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		Optional<Favorite> optionalFavorite = favoriteRepository.findByUserAndFavoriteUser(findUser, findFavoriteUser);
		if (optionalFavorite.isPresent()) {
			throw new DuplicateFavoriteUserException("Duplicated favorite user");
		} else {

			Favorite favorite = Favorite.builder()
					.user(findUser)
					.favoriteUser(findFavoriteUser)
					.build();

			Favorite savedFavorite = favoriteRepository.save(favorite);
			findUser.addFavorite(savedFavorite);
			userRepository.save(findUser);
		}
		return getFavorites(findUser);
	}


	public HashSet<ProfileCardDto.SimpleResponse> favoriteList(String email) {
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email));

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
	
	@Transactional
	public void updateGithub(String email, String githubId){
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		Optional<Github> optionalGithub = githubRepository.findByUser(findUser);
		if(optionalGithub.isPresent()){
			githubRepository.delete(optionalGithub.get());
		}
		try {
			Github github = Github.from(parsing(githubId));
			github.setUser(findUser);
			findUser.setGithub(github);
			githubRepository.save(github);
		}catch (Exception e){
			throw new RuntimeException("잠시 후에 다시 등록해주세요");
		}
	}

	@Transactional
	public void deleteGithub(String email) {
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		Github findGithub = githubRepository.findByUser(findUser)
				.orElseThrow(() -> new UserException(ErrorCode.GITHUB_NOT_FOUND));
		githubRepository.delete(findGithub);
	}

	public GithubDto parsing(String githubId) throws IOException, ParseException, java.text.ParseException {
		JSONParser parser = new JSONParser();

		URL mainUrl = new URL(githubApi + separator + githubId);

		BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
		String result = br.readLine();
		JSONObject o1 = (JSONObject) parser.parse(result);

		URL subUrl = new URL(githubApi + separator + githubId + "/events");
		BufferedReader subBr = new BufferedReader(new InputStreamReader(subUrl.openStream(), StandardCharsets.UTF_8));
		String subResult = subBr.readLine();

		JSONArray jsonArray = (JSONArray) parser.parse(subResult);
		String message = "";
		String date = "";


		for (Object o : jsonArray) {
			JSONObject o2 = (JSONObject) o;

			if (o2.get("type").equals("PushEvent")) {
				date = (String) o2.get("created_at");
				Object payload = o2.get("payload");
				JSONObject payload1 = (JSONObject) payload;
				Object commits = payload1.get("commits");
				JSONArray commits1 = (JSONArray) commits;

				if (commits1.size() > 0) {
					Object o3 = commits1.get(0);
					JSONObject o31 = (JSONObject) o3;
					message = (String) o31.get("message");
					break;
				}
			}
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate realDate = simpleDateFormat.parse(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return GithubDto.builder()
				.name((String) o1.get("name"))
				.login((String) o1.get("login"))
				.githubId((Long) o1.get("id"))
				.githubUrl((String) o1.get("url"))
				.following((Long) o1.get("following"))
				.followers((Long) o1.get("followers"))
				.location((String) o1.get("location"))
				.imageUrl((String) o1.get("avatar_url"))
				.recentCommitAt(realDate)
				.recentCommitMessage(message)
				.build();
	}

	public void sendUpdateEmail(String userEmail, String updateEmail) {
		mailComponent.sendUpdateEmail(updateEmail, MailMessage.EMAIL_EMAIL_UPDATE, MailMessage.setEmailUpdateMessage(updateEmail));
	}


	private User verifyUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + "not found"));
	}

	private boolean verifyPassword(String rawPassword, String cryptPassword) {
		if (!passwordEncoder.matches(rawPassword, cryptPassword)) {
			throw new UserException(ErrorCode.WRONG_USER_PASSWORD);
		}

		return true;
	}

	public UserDto.DefaultResponse getUserInfo(String userEmail) {
		User findUser = verifyUserByEmail(userEmail);
		UserDto.DefaultResponse userInfo = UserDto.DefaultResponse.from(findUser);
		userInfo.setProfileImage(server + separator + profilePath + separator + userEmail);
		return userInfo;
	}

	@Transactional
	public void dummy() {
		for (int i = 0; i < 100; i++) {
			User user = User.builder().userStatus(UserStatus.ACTIVE).role(Role.USER).build();
			user.setEmail("test" + i + "@test.com");
			user.setNickname("test" + i);
			user.setPassword(passwordEncoder.encode("test" + i));
			userRepository.save(user);
		}

	}
}

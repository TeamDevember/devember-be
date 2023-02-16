package com.gridians.gridians.domain.card.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.entity.Skill;
import com.gridians.gridians.domain.card.entity.Sns;
import com.gridians.gridians.domain.card.entity.Tag;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.*;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.user.dto.GithubDto;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.Github;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.GithubRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileCardService {

	private final UserRepository userRepository;
	private final SkillRepository skillRepository;
	private final CommentRepository commentRepository;
	private final SnsRepository snsRepository;
	private final TagRepository tagRepository;
	private final FieldRepository fieldRepository;
	private final FavoriteRepository favoriteRepository;
	private final ProfileCardRepository profileCardRepository;
	private final GithubRepository githubRepository;

	@Value("${server.host.api}")
	private String server;

	@Value("${custom.path.github}")
	private String githubApi;

	@Value("${custom.path.profileApi}")
	private String profileApi;

	@Value("${custom.path.skillApi}")
	private String skillApi;

	//프로필 카드 생성
	@Transactional
	public ProfileCard createProfileCard(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Optional<ProfileCard> findPc = profileCardRepository.findByUser(user);
		if (findPc.isPresent()) {
			throw new CardException(ErrorCode.DUPLICATED_USER);
		}

		ProfileCard pc = ProfileCard.from();
		pc.setUser(user);
		ProfileCard savedPc = profileCardRepository.save(pc);
		user.setProfileCard(savedPc);
		return savedPc;
	}

	@Transactional
	public void input(String email, Long id, ProfileCardDto.Request request) throws IOException {
		userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		saveField(pc, request);
		saveSnsSet(pc, request);
		saveSkill(pc, request);
		saveTagSet(pc, request);
		pc.setIntroduction(request.getIntroduction());
		pc.setStatusMessage(request.getStatusMessage());

		profileCardRepository.save(pc);
	}

	//카드 상세 정보
	public ProfileCardDto.DetailResponse readProfileCard(Long id) throws IOException {
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		List<Comment> commentList = commentRepository.findAllByProfileCardOrderByCreatedAtDesc(pc);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();

		for (Comment comment : commentList) {
			CommentDto.Response response = CommentDto.Response.from(comment);
			response.setProfileImage(comment.getUser().getId().toString());
			commentDtoList.add(response);
		}
		ProfileCardDto.DetailResponse detailResponse;
		if (pc.getUser().getGithub() != null) {
			Github github = pc.getUser().getGithub();
			detailResponse = ProfileCardDto.DetailResponse.from(github, pc, commentDtoList);
		} else {
			detailResponse = ProfileCardDto.DetailResponse.from(pc, commentDtoList);
		}
		detailResponse.setProfileImage(server + "/profile-image/" + pc.getUser().getEmail());
		detailResponse.setSkillImage(pc.getSkill() == null ?
				server + "/" + skillApi + "/default" :
				server + "/" + skillApi + "/" + pc.getSkill().getName().toLowerCase()
		);
		return detailResponse;
	}

	//카드 리스트 조회
	public List<ProfileCardDto.SimpleResponse> allProfileCardList(int page, int size) throws IOException {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ProfileCard> pcList = profileCardRepository.findAllByOrderByCreatedAtDesc(pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();
		for (ProfileCard pc : pcList) {
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(pc);
			simpleResponse.setProfileImage(server + "/" + profileApi + "/" + pc.getUser().getEmail());
			simpleResponse.setSkillImage(pc.getSkill() == null ?
					server + "/" + skillApi + "/default" :
					server + "/" + skillApi + "/" + pc.getSkill().getName().toLowerCase()
			);
			profileCardList.add(simpleResponse);
		}
		log.info("size = {}", profileCardList.size());
		return profileCardList;
	}

	public List<ProfileCardDto.SimpleResponse> favoriteCardList(String email, int page, int size) throws IOException {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Favorite> favorites = favoriteRepository.findAllByUser(user, pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();

		for (Favorite favorite : favorites) {
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(favorite.getUser().getProfileCard());
			simpleResponse.setProfileImage(server + "/" + profileApi + "/" + favorite.getUser().getEmail());
			profileCardList.add(simpleResponse);
		}
		return profileCardList;
	}

	@Transactional
	public void saveField(ProfileCard pc, ProfileCardDto.Request request) {
		pc.setField(fieldRepository.findByName(request.getField())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND)));
	}

	@Transactional
	public void saveSnsSet(ProfileCard pc, ProfileCardDto.Request request) {

		snsRepository.deleteAllInBatch(pc.getSnsSet());
		Set<ProfileCardDto.SnsResponse> sSet = request.getSnsSet();
		for (ProfileCardDto.SnsResponse snsResponse : sSet) {
			pc.addSns(Sns.from(pc, snsResponse.getName(), snsResponse.getAccount()));
		}
	}

	@Transactional
	public void saveTagSet(ProfileCard pc, ProfileCardDto.Request request) {
		tagRepository.deleteAllInBatch(pc.getTagList());
		Set<String> requestTagSet = request.getTagSet();
		for (String s : requestTagSet) {
			pc.addTag(Tag.from(s));
		}
	}

	@Transactional
	public void saveSkill(ProfileCard pc, ProfileCardDto.Request request) {
		Skill skill = skillRepository.findByName(request.getSkill())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
		skill.addProfileCard(pc);
	}

	@Transactional
	public ProfileCard deleteProfileCard(String email, Long id) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id).orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));

		if (user != pc.getUser()) {
			throw new RuntimeException("본인만 삭제할 수 있습니다.");
		}

		profileCardRepository.delete(pc);
		return pc;
	}

	@Transactional
	public void saveGithub(String email, String githubId) throws IOException, ParseException, java.text.ParseException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		if (user.getGithub() != null) {
			Github github = user.getGithub();
			user.setGithub(null);
			githubRepository.delete(github);
		}
		Github github = Github.from(parsing(githubId));
		github.setUser(user);
		Github savedGithub = githubRepository.save(github);
		user.setGithub(savedGithub);
	}

	@Transactional
	public void deleteGithub(String email) throws IOException, ParseException, java.text.ParseException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		Github github = user.getGithub();
		user.setGithub(null);
		githubRepository.delete(github);
	}

	public GithubDto parsing(String githubId) throws IOException, ParseException, java.text.ParseException {
		JSONParser parser = new JSONParser();

		URL mainUrl = new URL(githubApi + "/" + githubId);

		BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
		String result = br.readLine();
		JSONObject o1 = (JSONObject) parser.parse(result);

		URL subUrl = new URL(githubApi + "/" + githubId + "/events");
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
}
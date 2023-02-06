package com.gridians.gridians.domain.card.service;

import com.gridians.gridians.domain.card.dto.GithubDto;
import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.dto.ProfileCardDto.SnsResponse;
import com.gridians.gridians.domain.card.entity.*;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.*;
import com.gridians.gridians.domain.card.type.CardErrorCode;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.FavoriteRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
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
@RequiredArgsConstructor
public class ProfileCardService {

	private final UserRepository userRepository;
	private final GithubRepository githubRepository;
	private final SkillRepository skillRepository;
	private final CommentRepository commentRepository;
	private final SnsRepository snsRepository;
	private final TagRepository tagRepository;
	private final FieldRepository fieldRepository;
	private final FavoriteRepository favoriteRepository;
	private final ProfileCardRepository profileCardRepository;

	//프로필 카드 생성

	@Transactional
	public ProfileCard createProfileCard(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		if(profileCardRepository.findByUser(user).isPresent()){
			throw new UserException(UserErrorCode.DUPLICATED_USER);
		}
		ProfileCard pc = ProfileCard.from();
		pc.setUser(user);
		ProfileCard saveProfileCard = profileCardRepository.save(pc);
		userRepository.save(user);
		return saveProfileCard;
	}

	// 프로필 카드 기입

	@Transactional
	public void input(String email, Long id, ProfileCardDto.Request request) throws IOException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

//		saveFile(pc.getUser(), multipartFile);
		saveField(pc, request);
		saveSnsSet(pc, request);
		saveSkill(pc, request);
		saveTagSet(pc, request);
		pc.setIntroduction(request.getIntroduction());
		pc.setStatusMessage(request.getStatusMessage());
		pc.setUser(user);

		profileCardRepository.save(pc);
	}

	@Transactional
	public ProfileCardDto.DetailResponse readProfileCard(Long id) {

		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

//		if(pc.getUser().getGithubNumberId() != null){
//			Optional<Github> findGithub = githubRepository.findByProfileCard(pc);
//			Github github = findGithub.get();
//		}

		List<Comment> commentList = commentRepository.findAllByProfileCard(pc);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();

		for (Comment comment : commentList) {
			commentDtoList.add(CommentDto.Response.from(comment));
		}

		return ProfileCardDto.DetailResponse.from(pc, commentDtoList);
	}

	@Transactional
	public List<ProfileCardDto.SimpleResponse> allProfileCardList(int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ProfileCard> pcList = profileCardRepository.findAll(pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();
		for (ProfileCard pc : pcList) {
			profileCardList.add(ProfileCardDto.SimpleResponse.from(pc));
		}
		return profileCardList;
	}

	@Transactional
	public List<ProfileCardDto.SimpleResponse> favoriteCardList(String email, int page, int size) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Favorite> favorites = favoriteRepository.findAllByUser(user, pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();

		for (Favorite favorite : favorites) {
			profileCardList.add(ProfileCardDto.SimpleResponse.from(favorite.getUser().getProfileCard()));
		}
		return profileCardList;
	}

	@Transactional
	public void saveField(ProfileCard pc, ProfileCardDto.Request request) {
		pc.setField(fieldRepository.findByName(request.getField())
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND)));
	}

	@Transactional
	public void saveSnsSet(ProfileCard pc, ProfileCardDto.Request request) {

		snsRepository.deleteAllInBatch(pc.getSnsSet());
		Set<SnsResponse> sSet = request.getSnsSet();
		for (SnsResponse snsResponse : sSet) {
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
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		skill.addProfileCard(pc);
	}

	@Transactional
	public ProfileCard deleteProfileCard(Long id) {
		ProfileCard pc = profileCardRepository.findById(id).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		profileCardRepository.delete(pc);
		return pc;
	}

	@Transactional
	public void saveGithub(GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		ProfileCard pc = profileCardRepository.findById(request.getProfileCardId()).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		Github github = Github.from(parsing(request.getGithubId()));
		github.setProfileCard(pc);
		Github savedGithub = githubRepository.save(github);
		pc.setGithub(savedGithub);
	}

	public void deleteGithub(GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		ProfileCard pc = profileCardRepository.findById(request.getProfileCardId()).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		Github github = pc.getGithub();
		githubRepository.delete(github);
	}

	public GithubDto parsing(String githubId) throws IOException, ParseException, java.text.ParseException {
		JSONParser parser = new JSONParser();

		URL mainUrl = new URL("https://api.github.com/users/" + githubId);

		BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
		String result = br.readLine();
		JSONObject o1 = (JSONObject) parser.parse(result);

		URL subUrl = new URL("https://api.github.com/users/" + githubId + "/events");
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


	public void saveFile(User user, MultipartFile multipartFile) throws IOException {

		String originalName = multipartFile.getOriginalFilename();
		String uuid = user.getId().toString();
		String extension = originalName.substring(originalName.lastIndexOf("."));
		String saveName = uuid + extension;
		String savePath = "/Users/j/j/images/" + saveName;

		multipartFile.transferTo(new File(savePath));

	}
}
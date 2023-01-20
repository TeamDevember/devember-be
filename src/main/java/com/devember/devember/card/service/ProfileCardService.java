package com.devember.devember.card.service;

import com.devember.devember.card.dto.GithubDto;
import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.dto.ProfileCardDto.SnsDto;
import com.devember.devember.card.entity.*;
import com.devember.devember.card.exception.CardException;
import com.devember.devember.card.repository.*;
import com.devember.devember.card.type.CardErrorCode;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.exception.UserException;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileCardService {

	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;
	private final GithubRepository githubRepository;
	private final SkillRepository skillRepository;
	private final SnsRepository snsRepository;
	private final TagRepository tagRepository;
	private final FieldRepository fieldRepository;
	private final ProfileCardSkillRepository profileCardSkillRepository;
	private final ProfileCardTagRepository profileCardTagRepository;

	@Transactional
	public void createProfileCard(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		if (profileCardRepository.findByUser(user).isPresent()) {
			throw new UserException(UserErrorCode.DUPLICATED_USER);
		}
		ProfileCard pc = new ProfileCard();
		pc.setUser(user);
		profileCardRepository.save(pc);
	}

	@Transactional
	public void inputData(Long id, ProfileCardDto.updateRequest request) {
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		saveField(pc, request);
		saveSnsList(pc, request);
		saveSkillList(pc, request);
		saveTagList(pc, request);
		pc.setStatusMessage(request.getStatusMessage());

		profileCardRepository.save(pc);
	}


	// TODO: Profile Card에 들어가는 값들
	// 1. Detail (상태메세지)
	// 2. Field (활동 분야)
	// 3. List<ProfileCardSkill> (주 사용 스킬)
	// 4. List<ProfileCardTag> (태그)
	// 5. List<Sns> (SNS)

	// PC가 저장되기 전에 영속성 컨텍스트 항상 Clear() ? -> ID가 계속해서 증가함.. 새로운 게 Insert 된다는 소리
	// 그렇다면 어떻게?


	@Transactional
	public ProfileCardDto.ReadResponse readProfileCard(Long id) {

		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		return ProfileCardDto.ReadResponse.from(
				pc.getStatusMessage(),
				pc.getField(),
				pc.getProfileCardSkillList(),
				pc.getSnsList(),
				pc.getProfileCardTagList()
		);
	}

	public void saveField(ProfileCard pc, ProfileCardDto.updateRequest request) {
		pc.setField(fieldRepository.findByName(request.getField()).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND)));
	}

	public void saveSnsList(ProfileCard pc, ProfileCardDto.updateRequest request) {
		snsRepository.deleteAllByProfileCard(pc);

		List<SnsDto> sList = request.getSnsList();
		List<Sns> snsList = new ArrayList<>();

		for (SnsDto snsDto : sList) {
			Sns sns = snsRepository.save(Sns.from(pc, snsDto.getName(), snsDto.getAccount()));
			snsList.add(sns);
		}
		pc.setSnsList(snsList);
	}

	public void saveTagList(ProfileCard pc, ProfileCardDto.updateRequest request) {

		profileCardTagRepository.deleteAllByProfileCard(pc);

		List<String> tList = request.getTagList();
		List<ProfileCardTag> profileCardTagList = new ArrayList<>();


		for (String s : tList) {
			Tag tag = tagRepository.save(Tag.from(s));
			ProfileCardTag profileCardTag = profileCardTagRepository.save(ProfileCardTag.from(pc, tag));
			profileCardTagList.add(profileCardTag);
		}
		pc.setProfileCardTagList(profileCardTagList);
	}

	public void saveSkillList(ProfileCard pc, ProfileCardDto.updateRequest request) {
		profileCardSkillRepository.deleteAllByProfileCard(pc);

		List<String> sList = request.getSkillList();
		List<ProfileCardSkill> profileCardSkillList = new ArrayList<>();

		for (String s : sList) {
			Skill skill = skillRepository.findByName(s).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
			ProfileCardSkill profileCardSkill = profileCardSkillRepository.save(ProfileCardSkill.from(pc, skill));
			profileCardSkillList.add(profileCardSkill);
		}
		pc.setProfileCardSkillList(profileCardSkillList);
	}


	@Transactional
	public void deleteProfileCard(Long id) {
		profileCardRepository.deleteById(id);
	}

	public void saveGithubInfo(Long profileCardId, String githubId) throws IOException, ParseException, java.text.ParseException {

		ProfileCard pc = profileCardRepository.findById(profileCardId).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		pc.setGithub(Github.from(parsing(profileCardId, githubId)));
		profileCardRepository.save(pc);
	}

	public GithubDto parsing(Long profileCardId, String githubId) throws IOException, ParseException, java.text.ParseException {
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
				.id((Long) o1.get("id"))
				.githubUrl((String) o1.get("url"))
				.following((Long) o1.get("following"))
				.followers((Long) o1.get("followers"))
				.location((String) o1.get("location"))
				.imageUrl((String) o1.get("avatar_url"))
				.recentCommitAt(realDate)
				.recentCommitMessage(message)
				.build();
	}

	public void deleteGithub(Long id) {

		ProfileCard pc = profileCardRepository.findById(id).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		githubRepository.deleteById(pc.getGithub().getId());
	}


}



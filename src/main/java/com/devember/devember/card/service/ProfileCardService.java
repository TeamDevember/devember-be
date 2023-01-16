package com.devember.devember.card.service;

import com.devember.devember.card.dto.ProfileCardDto;
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileCardService {

	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;
	private final SnsRepository snsRepository;
	private final SkillRepository skillRepository;
	private final FieldRepository fieldRepository;
	private final DetailRepository detailRepository;
	private final GithubRepository githubRepository;

	// TODO: USER 권한 설정 필요

	@Transactional
	public void createProfileCard(ProfileCardDto.CardRequest request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		if (profileCardRepository.findByUser(user).isPresent()) {
			throw new UserException(UserErrorCode.DUPLICATED_USER);
		}

		ProfileCard pc = new ProfileCard();
		pc.setUser(user);
		user.setProfileCard(pc);
		profileCardRepository.save(pc);
	}

	@Transactional
	public void addSns(ProfileCardDto.SnsRequest request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Optional<Sns> findSns = snsRepository.findByName(request.getSns());

		if (findSns.isPresent()) {
			Sns getSns = findSns.get();
			getSns.setAccount(request.getAccount());
			pc.addSns(getSns);
		} else {

			Sns sns = Sns.from(request.getSns(), request.getAccount());
			sns.setProfileCard(pc);
			pc.addSns(sns);
			profileCardRepository.save(pc);
		}
		profileCardRepository.save(pc);
		Set<Sns> snsSet = pc.getSnsSet();
	}

	@Transactional
	public void addSkill(ProfileCardDto.SkillRequest request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Optional<Skill> findSkill = skillRepository.findByName(request.getSkill());
		Skill skill;

		if (findSkill.isPresent()) {
			skill = findSkill.get();
			skill.setName(request.getSkill());

		} else {
			skill = Skill.from(request.getSkill());
			skill.setProfileCard(pc);
		}

		pc.addSkill(skill);
		profileCardRepository.save(pc);
	}

	@Transactional
	public void addField(ProfileCardDto.FieldRequest request) {

		// 유저 검토
		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 유저 이름으로 프로필 카드 검색
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


		// 중복 검토 -> 정보 없으면 추가
		Optional<Field> findField = fieldRepository.findByProfileCard(pc);
		Field field;
		if (findField.isPresent()) {
			field = findField.get();
			field.setName(request.getField());
		} else {
			field = Field.from(request.getField());
		}
		pc.setField(field);
		profileCardRepository.save(pc);

	}

	@Transactional
	public ProfileCardDto.ReadResponse read(Long id) {
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		return ProfileCardDto.ReadResponse.from(pc);
	}

	@Transactional
	public void addDetail(ProfileCardDto.DetailRequest request) {
		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		ProfileCard pc = profileCardRepository.findByUser(user)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Optional<Detail> findDetail = detailRepository.findByProfileCard(pc);
		Detail detail;
		if(findDetail.isPresent()){
			detail = findDetail.get();
			detail.setStatus(request.getStatus());
			detail.setStatusMessage(request.getStatusMessage());
		} else {
			detail = Detail.from(request);
		}
		detail.setProfileCard(pc);
		pc.setDetail(detail);
		profileCardRepository.save(pc);
	}

	public void deleteSns(ProfileCardDto.DeleteSns request){
		Sns sns = snsRepository.findByName(request.getSns())
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		snsRepository.delete(sns);
	}

	public void deleteSkill(ProfileCardDto.DeleteSkill request){
		Skill sns = skillRepository.findByName(request.getSkill())
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));
		skillRepository.delete(sns);
	}
	public void deleteField(ProfileCardDto.DeleteField request){
		User user = userRepository.findById(request.getUser().getId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		//TODO: 다른 방법 검토
		pc.setField(null);
		profileCardRepository.save(pc);
	}


	public void saveGithubInfo(String githubId) throws IOException, ParseException {
		JSONParser parser = new JSONParser();

		URL mainUrl = new URL("https://api.github.com/users/" + githubId);

		BufferedReader br = new BufferedReader(new InputStreamReader(mainUrl.openStream(), StandardCharsets.UTF_8));
		String result = br.readLine();
		JSONObject o1 = (JSONObject) parser.parse(result);

		String name = (String) o1.get("name");
		String login = (String) o1.get("login");
		Long id = (Long) o1.get("id");
		String githubUrl = (String) o1.get("url");
		Long followers = (Long) o1.get("followers");
		Long following = (Long) o1.get("following");
		String location = (String) o1.get("location");
		String imageUrl = (String) o1.get("avatar_url");

		URL subUrl = new URL("https://api.github.com/users/"+ githubId +"/events");
		BufferedReader subBr = new BufferedReader(new InputStreamReader(subUrl.openStream(), StandardCharsets.UTF_8));
		String subResult = subBr.readLine();

		JSONArray jsonArray = (JSONArray) parser.parse(subResult);
		String message = "";
		String date = "";

		for (Object o : jsonArray) {
			JSONObject o2 = (JSONObject) o;

			if(o2.get("type").equals("PushEvent")) {
				date = (String) o2.get("created_at");
				Object payload = o2.get("payload");
				JSONObject payload1 = (JSONObject) payload;
				Object commits = payload1.get("commits");
				JSONArray commits1 = (JSONArray) commits;

				if(commits1.size() > 0){
					Object o3 = commits1.get(0);
					JSONObject o31 = (JSONObject) o3;
					message = (String) o31.get("message");
					break;
				}
			}
		}

		Github github = new Github();
		github.setName(name);
		github.setLogin(login);
		github.setId(id);
		github.setLocation(location);
		github.setUrl(githubUrl);
		github.setFollowersUrl(followers);
		github.setFollowingUrl(following);
		github.setProfileImageUrl(imageUrl);
		github.setRecentCommitAt(date);
		github.setRecentCommitMessage(message);

		githubRepository.save(github);
	}
}



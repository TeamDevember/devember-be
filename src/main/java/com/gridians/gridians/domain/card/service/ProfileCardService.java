package com.gridians.gridians.domain.card.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.*;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.*;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Value("${custom.path.profile}")
	private String profilePath;

	@Value("${custom.path.skill}")
	private String skillPath;

	private String separator = "/";
	private String defaultValue = "default";

	//프로필 카드 생성
	@Transactional
	public ProfileCard createProfileCard(String email) {
		User findUser = verifyUserByEmail(email);
		Optional<ProfileCard> findProfileCard = profileCardRepository.findByUser(findUser);
		if (findProfileCard.isPresent()) {
			throw new CardException(ErrorCode.DUPLICATED_USER);
		}

		if(findUser.getGithubNumberId() != null){

		}

		ProfileCard pc = ProfileCard.from();
		pc.setUser(findUser);

		ProfileCard savedPc = profileCardRepository.save(pc);
		findUser.setProfileCard(savedPc);
		return savedPc;
	}

	@Transactional
	public void input(String email, Long profileCardId, ProfileCardDto.Request request) {
		User findUser = verifyUserByEmail(email);
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);

		saveField(findProfileCard, request);
		saveSnsSet(findProfileCard, request);
		saveSkill(findProfileCard, request);
		saveTagSet(findProfileCard, request);
		findProfileCard.setIntroduction(request.getIntroduction());
		findProfileCard.setStatusMessage(request.getStatusMessage());

		profileCardRepository.save(findProfileCard);
	}

	public ProfileCardDto.DetailResponse readProfileCard(Long profileCardId) {
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);
		List<Comment> findCommentList = commentRepository.findAllByProfileCardOrderByCreatedAtDesc(findProfileCard);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();

		for (Comment comment : findCommentList) {
			CommentDto.Response response = CommentDto.Response.from(comment);
			response.setProfileImage(server + separator + profilePath + separator + findProfileCard.getUser().getEmail());
			commentDtoList.add(response);
		}
		ProfileCardDto.DetailResponse detailResponse;
		Optional<Github> optionalGithub = githubRepository.findByUser(findProfileCard.getUser());

		if (optionalGithub.isPresent()) {
			Github findGithub = optionalGithub.get();
			detailResponse = ProfileCardDto.DetailResponse.from(findGithub, findProfileCard, commentDtoList);
		} else {
			detailResponse = ProfileCardDto.DetailResponse.from(findProfileCard, commentDtoList);
		}
		detailResponse.setProfileImage(server + separator + profilePath + separator + findProfileCard.getUser().getEmail());
		detailResponse.setSkillImage(findProfileCard.getSkill() == null ?
				server + separator + skillPath + separator + defaultValue :
				server + separator + skillPath + separator + findProfileCard.getSkill().getName().toLowerCase()
		);
		return detailResponse;
	}

	public ProfileCardDto.DetailResponse getMyCard(String email){
		User user = verifyUserByEmail(email);
		ProfileCard profileCard = verifyProfileCardById(user.getProfileCard().getId());
		return readProfileCard(profileCard.getId());
	}

	public List<ProfileCardDto.SimpleResponse> allProfileCardList(int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ProfileCard> findProfileCardList = profileCardRepository.findAllByOrderByCreatedAtDesc(pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();
		for (ProfileCard pc : findProfileCardList) {
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(pc);
			simpleResponse.setProfileImage(server + separator + profilePath + separator + pc.getUser().getEmail());
			simpleResponse.setSkillImage(pc.getSkill() == null ?
					server + separator + skillPath + separator + defaultValue :
					server + separator + skillPath + separator + pc.getSkill().getName().toLowerCase()
			);
			profileCardList.add(simpleResponse);
		}
		log.info("size = {}", profileCardList.size());
		return profileCardList;
	}

	public List<ProfileCardDto.SimpleResponse> favoriteCardList(String email, int page, int size) {

		User findUser = verifyUserByEmail(email);

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Favorite> findFavoriteList = favoriteRepository.findAllByUser(findUser, pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();

		for (Favorite favorite : findFavoriteList) {
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(favorite.getUser().getProfileCard());
			simpleResponse.setProfileImage(server + separator + profilePath + separator + favorite.getUser().getEmail());
			profileCardList.add(simpleResponse);
		}
		return profileCardList;
	}

	@Transactional
	public void saveField(ProfileCard pc, ProfileCardDto.Request request) {
		Field findField = fieldRepository.findByName(request.getField())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
		pc.setField(findField);
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
		Skill findSkill = skillRepository.findByName(request.getSkill())
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
		findSkill.addProfileCard(pc);
	}

	@Transactional
	public ProfileCard deleteProfileCard(String email) {
		User findUser = verifyUserByEmail(email);
		ProfileCard findProfileCard = verifyProfileCardById(findUser.getProfileCard().getId());

		if (findUser != findProfileCard.getUser()) {
			throw new CardException(ErrorCode.DELETE_ONLY_OWNER);
		}

		profileCardRepository.delete(findProfileCard);
		return findProfileCard;
	}

	public User verifyUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
	}

	public ProfileCard verifyProfileCardById(Long profileCardId) {
		return profileCardRepository.findById(profileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
	}
}
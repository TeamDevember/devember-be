package com.gridians.gridians.domain.card.service;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.dto.ProfileCardDto.SnsResponse;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.entity.Skill;
import com.gridians.gridians.domain.card.entity.Sns;
import com.gridians.gridians.domain.card.entity.Tag;
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
import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
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
	private final S3Service s3Service;


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
	public ProfileCardDto.DetailResponse readProfileCard(String email, Long id) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

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

		ProfileCardDto.DetailResponse detailResponse = ProfileCardDto.DetailResponse.from(pc, commentDtoList);
		detailResponse.setImageSrc(s3Service.getProfileImage(user.getId().toString()));

		return detailResponse;
	}

	@Transactional
	public List<ProfileCardDto.SimpleResponse> allProfileCardList(int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ProfileCard> pcList = profileCardRepository.findAll(pageRequest);

		List<ProfileCardDto.SimpleResponse> profileCardList = new ArrayList<>();
		for (ProfileCard pc : pcList) {
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(pc);
			simpleResponse.setImageSrc(s3Service.getProfileImage(pc.getUser().getId().toString()));
			profileCardList.add(simpleResponse);
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
			ProfileCardDto.SimpleResponse simpleResponse = ProfileCardDto.SimpleResponse.from(favorite.getUser().getProfileCard());
			simpleResponse.setImageSrc(s3Service.getProfileImage(favorite.getUser().getId().toString()));
			profileCardList.add(simpleResponse);
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

	public void saveFile(User user, MultipartFile multipartFile) throws IOException {

		String originalName = multipartFile.getOriginalFilename();
		String uuid = user.getId().toString();
		String extension = originalName.substring(originalName.lastIndexOf("."));
		String saveName = uuid + extension;
		String savePath = "/Users/j/j/images/" + saveName;

		multipartFile.transferTo(new File(savePath));

	}
}
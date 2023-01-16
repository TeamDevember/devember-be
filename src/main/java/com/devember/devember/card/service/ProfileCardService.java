package com.devember.devember.card.service;

import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.entity.Field;
import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Skill;
import com.devember.devember.card.entity.Sns;
import com.devember.devember.card.exception.CardException;
import com.devember.devember.card.repository.FieldRepository;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.card.repository.SkillRepository;
import com.devember.devember.card.repository.SnsRepository;
import com.devember.devember.card.type.CardErrorCode;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.exception.UserException;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileCardService {

	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;
	private final SnsRepository snsRepository;
	private final SkillRepository skillRepository;
	private final FieldRepository fieldRepository;

	// TODO: USER 권한 설정 필요

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

	public void addField(ProfileCardDto.FieldRequest request) {

		// 유저 검토
		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 유저 이름으로 프로필 카드 검색
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


		// 중복 검토 -> 정보 없으면 추가
		Optional<Field> findField = fieldRepository.findByProfileCard(pc);
		Field field;
		if(findField.isPresent()){
			field = findField.get();
			field.setName(request.getField());
		} else {
			field = Field.from(request.getField());
		}
		field.setProfileCard(pc);
		pc.setField(field);
		profileCardRepository.save(pc);

	}

	public void read(Long id) {
		ProfileCard profileCard = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		profileCard.getDetail();

	}
}

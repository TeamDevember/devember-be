package com.devember.devember.card.service;

import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.entity.Field;
import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Skill;
import com.devember.devember.card.entity.Sns;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.card.repository.SkillRepository;
import com.devember.devember.card.repository.SnsRepository;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.exception.UserException;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileCardService {

	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;
	private final SnsRepository snsRepository;
	private final SkillRepository skillRepository;

	public void createProfileCard(ProfileCardDto.createCard request) {

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


	public void snsUpdate(ProfileCardDto.snsUpdate request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Optional<Sns> savedSns = snsRepository.findByName(request.getName());

		if (savedSns.isPresent()) {
			Sns sns = savedSns.get();
			sns.setAccount(request.getAccount());
			sns.setProfileCard(pc);
			pc.getSnsList().add(savedSns.get());
		} else {
			Sns sns = Sns.from(request.getName(), request.getAccount());
			sns.setProfileCard(pc);
			pc.getSnsList().add(sns);
		}

		profileCardRepository.save(pc);
	}

	public void skillUpdate(ProfileCardDto.skillUpdate request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		List<Skill> skillList = new ArrayList<>();
		for ( String s : request.getSkillList()) {
			Skill skill = Skill.from(s);
			skill.setProfileCard(pc);
			skillList.add(skill);
		}

		pc.setSkillList(skillList);
		profileCardRepository.save(pc);
	}

	public void fieldUpdate(ProfileCardDto.fieldUpdate request) {

		User user = userRepository.findById(request.getUser().getId())
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findByUser(user).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Field field = Field.from(request.getField());
		field.setProfileCard(pc);
		pc.setField(field);
		profileCardRepository.save(pc);

	}
}

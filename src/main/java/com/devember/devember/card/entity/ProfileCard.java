package com.devember.devember.card.entity;

import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@OneToOne
	private User user;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<Sns> snsList;

	@OneToOne
	private Detail detail;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<Skill> skillList;

	@OneToOne(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private Field field;


	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void updateSns(ProfileCardDto.snsUpdate request) {



	}
//
//	public void updateSkillList(ProfileCardDto.skillUpdate request) {
//
//		List<Skill> skillList = new ArrayList<>();
//		List<String> skills = request.getSkill();
//
//		for (String skill : skills) {
//			skillList.add(Skill.from(skill));
//		}
//
//		this.skillList = skillList;
//	}

	public void updateField(ProfileCardDto.fieldUpdate request) {
		this.field = Field.from(request.getField());
	}


}

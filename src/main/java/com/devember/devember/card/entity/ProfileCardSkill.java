package com.devember.devember.card.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCardSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_card_id")
	private ProfileCard profileCard;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id")
	private Skill skill;


	public void setProfileCard(ProfileCard profileCard) {
		this.profileCard = profileCard;
	}

	public void setSkill(Skill skill){
		this.skill = skill;
	}

	public static ProfileCardSkill from(ProfileCard profileCard, Skill skill){
		return ProfileCardSkill.builder()
				.profileCard(profileCard)
				.skill(skill)
				.build();
	}
}

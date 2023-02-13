package com.gridians.gridians.domain.card.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
	private List<ProfileCard> profileCardList;

	private String  name;

	public static Skill from(String name){
		return Skill.builder()
				.name(name)
				.build();
	}

	public void addProfileCard(ProfileCard profileCard){
		profileCard.setSkill(this);
		profileCardList.add(profileCard);
	}

}

package com.gridians.gridians.domain.card.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
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

	@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
	private List<ProfileCardSkill> profileCardSkillList = new ArrayList<>();

	private String  name;

	public static Skill from(String name){
		return Skill.builder()
				.name(name)
				.build();
	}
}

package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "profile_card_id")
	private ProfileCard profileCard;

	private String  name;

	public static Skill from(String name){
		return Skill.builder()
				.name(name)
				.build();
	}
}

package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
	private Set<Sns> snsSet = new HashSet<>();

	@OneToOne
	private Detail detail;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private Set<Skill> skillSet = new HashSet<>();

	@OneToOne(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private Field field;


	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void addSns(Sns sns){
		this.snsSet.add(sns);
	}

	public void addSkill(Skill skill){
		this.skillSet.add(skill);
	}

}

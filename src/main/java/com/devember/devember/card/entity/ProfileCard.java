package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = LAZY)
	private User user;

	@OneToMany(mappedBy = "profileCard",cascade = CascadeType.ALL)
	private Set<Sns> snsSet = new HashSet<>();

	@OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
	@JoinColumn(name = "detail_id")
	private Detail detail;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private Set<Skill> skillSet = new HashSet<>();

	@OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
	@JoinColumn(name = "field_id")
	private Field field;

	@OneToOne(mappedBy = "user", fetch = LAZY, cascade = CascadeType.ALL)
	private Github github;

	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void addSns(Sns sns){
		this.snsSet.add(sns);
		sns.setProfileCard(this);
	}

	public void addSkill(Skill skill){
		this.skillSet.add(skill);
		skill.setProfileCard(this);
	}

	public void setField(Field field){
		field.setProfileCard(this);
		this.field = field;
	}

	public void setDetail(Detail detail){
		detail.setProfileCard(this);
		this.detail = detail;
	}

	public void setUser(User user){
		user.setProfileCard(this);
		this.user = user;
	}
}

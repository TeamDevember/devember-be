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
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = LAZY)
	private User user;

	@OneToMany(mappedBy = "profileCard")
	private Set<Sns> snsSet = new HashSet<>();

	private String statusMessage;

	@OneToMany(mappedBy = "profileCard")
	private Set<ProfileCardSkill> profileCardSkillSet = new HashSet<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "field_id")
	private Field field;

	@OneToOne(mappedBy = "profileCard", fetch = FetchType.LAZY)
	private Github github;

	@OneToMany(mappedBy = "profileCard",cascade = CascadeType.ALL)
	private Set<ProfileCardTag> profileCardTagSet = new HashSet<>();

	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void addSns(Sns sns) {
		sns.setProfileCard(this);
		snsSet.add(sns);
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public void addProfileCardSkill(ProfileCardSkill profileCardSkill) {
		profileCardSkill.setProfileCard(this);
		profileCardSkillSet.add(profileCardSkill);
	}

	public void setProfileCardTagSet(ProfileCardTag profileCardTag) {
		profileCardTag.setProfileCard(this);
		profileCardTagSet.add(profileCardTag);
	}

	public void setField(Field field) {
		this.field = field;
		field.addProfileCard(this);
	}

	public void setUser(User user) {
		user.setProfileCard(this);
		this.user = user;
	}

	public void setGithub(Github github) {
		this.github = github;
	}
}

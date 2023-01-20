package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<Sns> snsList;

	private String statusMessage;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<ProfileCardSkill> profileCardSkillList;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "field_id")
	private Field field;

	@OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "github_id")
	private Github github;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<ProfileCardTag> profileCardTagList;

	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void setSnsList(List<Sns> snsList){
		this.snsList = snsList;
	}

	public void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}

	public void setProfileCardSkillList(List<ProfileCardSkill> profileCardSkillList){
		this.profileCardSkillList = profileCardSkillList;
	}

	public void setProfileCardTagList(List<ProfileCardTag> profileCardTagList){
		this.profileCardTagList = profileCardTagList;
	}

	public void setField(Field field){
		this.field = field;
		field.addProfileCard(this);
	}



	public void setUser(User user){
		user.setProfileCard(this);
		this.user = user;
	}

	public void setGithub(Github github){
		this.github = github;
	}
}

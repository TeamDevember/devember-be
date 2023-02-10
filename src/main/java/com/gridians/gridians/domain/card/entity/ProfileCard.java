package com.gridians.gridians.domain.card.entity;

import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.global.entity.BaseEntity;
import com.gridians.gridians.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.ALL)
	private List<Comment> commentList = new ArrayList<>();

	@OneToMany(mappedBy = "profileCard",cascade = CascadeType.ALL)
	private Set<Sns> snsSet = new HashSet<>();

	private String statusMessage;
	private String introduction;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "skill_id")
	private Skill skill;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "field_id")
	private Field field;

	@OneToMany(mappedBy = "profileCard",cascade = CascadeType.ALL)
	private Set<Tag> tagList = new HashSet<>();

	public static ProfileCard from() {

		return ProfileCard.builder()
				.build();
	}

	public void addSns(Sns sns) {
		sns.setProfileCard(this);
		snsSet.add(sns);
	}

	public void addComment(Comment comment) {
		comment.setProfileCard(this);
		commentList.add(comment);
	}

	public void addTag(Tag tag){
		tag.setProfileCard(this);
		tagList.add(tag);
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public void setField(Field field) {
		this.field = field;
		field.addProfileCard(this);
	}

	public void setSkill(Skill skill){
		this.skill = skill;
	}

}

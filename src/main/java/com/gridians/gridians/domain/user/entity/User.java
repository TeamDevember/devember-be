package com.gridians.gridians.domain.user.entity;


import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {
	public User() {}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)", name = "id")
	private UUID id;

	private String email;
	private String nickname;
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "create_type")
	private CreateType createType;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "github_id", unique = true)
	private Github github;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private ProfileCard profileCard;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Set<Favorite> favorites = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Comment> commentList = new ArrayList<>();

	@Column(unique = true)
	private Long githubNumberId;

	public static User from(JoinDto.Request request){
		return User.builder().email(request.getEmail())
				.nickname(request.getNickname())
				.userStatus(UserStatus.ACTIVE)
				.password(request.getPassword())
				.build();
	}

	//종명님 코드
//	public void addFavorite(Favorite favorite) {
//		for(Favorite fav : this.favorites) {
//			if(fav.getFavoriteUser() == favorite.getFavoriteUser()) {
//				throw new DuplicateFavoriteUserException("duplicate favorite user");
//			}
//		}
//		this.favorites.add(favorite);
//		favorite.setUser(this);
//	}

	public void addFavorite(Favorite favorite){
		this.favorites.add(favorite);
	}

	public void setGithub(Github github) {
		this.github = github;
	}

	public void deleteFavorite(Favorite favorite) {
		favorites.remove(favorite);
	}
}
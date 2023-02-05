package com.gridians.gridians.domain.user.entity;


import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

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

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "profile_card_id")
	private ProfileCard profileCard;

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = LAZY, cascade = CascadeType.ALL)
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

	public void addFavorite(Favorite favorite) {
		favorite.setUser(this);
		favorites.add(favorite);
	}
	public void deleteFavorite(Favorite favorite) {
		favorites.remove(favorite);
	}
}
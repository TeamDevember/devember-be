package com.gridians.gridians.domain.user.entity;


import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.type.UserStatus;
import com.gridians.gridians.global.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)", name = "id")
	private UUID id;

	@Column(unique = true)
	private Long githubNumberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "create_type")
	private CreateType createType;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private ProfileCard profileCard;


	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Favorite> favorites = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Comment> commentList = new ArrayList<>();

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private Github github;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private ProfileImage profileImage;


	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String nickname;
	private String password;

	public static User from(JoinDto.Request request){
		return User.builder().email(request.getEmail())
				.nickname(request.getNickname())
				.userStatus(UserStatus.ACTIVE)
				.password(request.getPassword())
				.build();
	}

	public void addFavorite(Favorite favorite){
		this.favorites.add(favorite);
	}

	public void deleteFavorite(Favorite favorite) {
		favorites.remove(favorite);
	}
}
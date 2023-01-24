package com.devember.devember.user.entity;


import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.dto.JoinDto;
import com.devember.devember.user.type.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.UUID;

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

	@OneToOne(mappedBy = "user", fetch = LAZY)
	private ProfileCard profileCard;




	public static User from(JoinDto.Request request){
		return User.builder().email(request.getEmail())
				.nickname(request.getNickname())
				.userStatus(UserStatus.ACTIVE)
				.password(request.getPassword())
				.build();
	}
}

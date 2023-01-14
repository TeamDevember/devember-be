package com.devember.devember.card.entity;

import com.devember.devember.user.entity.User;
import com.devember.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@OneToOne
	private User user;

	@OneToOne(mappedBy = "profileCard")
	private Sns sns;

	@OneToOne
	private Detail detail;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.REMOVE)
	private List<Skill> skillList;

	@OneToMany(mappedBy = "profileCard", cascade = CascadeType.REMOVE)
	private List<Field> fieldList;

}

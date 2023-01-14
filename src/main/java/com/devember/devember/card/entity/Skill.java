package com.devember.devember.card.entity;

import com.devember.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private ProfileCard profileCard;

	private String  name;
}

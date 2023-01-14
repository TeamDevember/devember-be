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
public class Sns extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private ProfileCard profileCard;

	private String twitter;
	private String notion;
	private String linkedin;
	private String instagram;
	private String facebook;

}

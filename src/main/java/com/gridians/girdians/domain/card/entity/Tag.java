package com.gridians.girdians.domain.card.entity;

import com.gridians.girdians.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "profile_card_id")
	private ProfileCard profileCard;

	public static Tag from(String name){
		return Tag.builder()
				.name(name)
				.build();
	}

	public void setProfileCard(ProfileCard profileCard){
		this.profileCard = profileCard;
	}
}

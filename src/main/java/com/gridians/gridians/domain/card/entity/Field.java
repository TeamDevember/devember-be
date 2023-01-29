package com.gridians.gridians.domain.card.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Field {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "field")
	private List<ProfileCard> profileCardList;

	private String name;

	public static Field from(String name){
		return Field.builder()
				.name(name)
				.build();
	}

	public void addProfileCard(ProfileCard profileCard){
		this.profileCardList = new ArrayList<>();
		profileCardList.add(profileCard);
	}
}

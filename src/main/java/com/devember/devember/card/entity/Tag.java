package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

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

	@OneToMany(mappedBy = "tag")
	private Set<ProfileCardTag> profileCardTagSet;

	public static Tag from(String name){
		return Tag.builder()
				.name(name)
				.build();
	}
}

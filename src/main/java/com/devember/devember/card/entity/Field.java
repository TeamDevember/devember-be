package com.devember.devember.card.entity;

import com.devember.devember.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Field extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	private ProfileCard profileCard;

	private String name;


	public static Field from(String name){
		return Field.builder()
				.name(name)
				.build();
	}
}

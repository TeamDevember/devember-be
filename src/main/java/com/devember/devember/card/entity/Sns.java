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
public class Sns extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private ProfileCard profileCard;

	private String name;
	private String account;

	public static Sns from(String name, String account){
		return Sns.builder()
				.name(name)
				.account(account)
				.build();
	}
}

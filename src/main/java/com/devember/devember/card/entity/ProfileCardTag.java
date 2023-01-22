package com.devember.devember.card.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class ProfileCardTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProfileCard profileCard;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Tag tag;

	public static ProfileCardTag from(ProfileCard profileCard, Tag tag){
		return ProfileCardTag.builder()
				.profileCard(profileCard)
				.tag(tag)
				.build();
	}

}

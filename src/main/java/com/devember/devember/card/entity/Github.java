package com.devember.devember.card.entity;


import com.devember.devember.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Github extends BaseEntity {

	@Id
	private Long id;

	private String name;
	private String Login;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	private String url;

	private LocalDate recentCommitAt;
	private String recentCommitMessage;

	private Long followers;
	private Long following;

	private String location;
	private String company;

}

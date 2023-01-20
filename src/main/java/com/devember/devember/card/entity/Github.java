package com.devember.devember.card.entity;


import com.devember.devember.card.dto.GithubDto;
import com.devember.devember.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.text.ParseException;
import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Github extends BaseEntity {

	@Id
	private Long id;

	private String name;
	private String login;

	private String profileImageUrl;

	private String url;

	@OneToOne(mappedBy = "github")
	private ProfileCard profileCard;

	private LocalDate recentCommitAt;
	private String recentCommitMessage;

	private Long followers;
	private Long following;

	private String location;
	private String company;


	public static Github from(GithubDto github) throws ParseException {

		return Github.builder()
				.id(github.getId())
				.name(github.getName())
				.login(github.getLogin())
				.profileImageUrl(github.getImageUrl())
				.url(github.getGithubUrl())
				.recentCommitAt(github.getRecentCommitAt())
				.recentCommitMessage(github.getRecentCommitMessage())
				.location(github.getLocation())
				.company(github.getCompany())
				.build();
	}
}

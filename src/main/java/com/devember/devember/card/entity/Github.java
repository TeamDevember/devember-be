package com.devember.devember.card.entity;


import com.devember.devember.card.dto.GithubDto;
import com.devember.devember.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Github extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long githubNumberId;

	private String name;
	private String login;

	private String profileImageUrl;

	private String url;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_card_id")
	private ProfileCard profileCard;

	private LocalDate recentCommitAt;
	private String recentCommitMessage;

	private Long followers;
	private Long following;

	private String location;
	private String company;


	public static Github from(GithubDto github) throws ParseException {

		return Github.builder()
				.githubNumberId(github.getGithubId())
				.name(github.getName())
				.login(github.getLogin())
				.profileImageUrl(github.getImageUrl())
				.url(github.getGithubUrl())
				.recentCommitAt(github.getRecentCommitAt())
				.recentCommitMessage(github.getRecentCommitMessage())
				.location(github.getLocation())
				.company(github.getCompany())
				.followers(github.getFollowers())
				.following(github.getFollowing())
				.build();
	}

	public void setAll(GithubDto github){
		this.githubNumberId = github.getGithubId();
		this.name = github.getName();
		this.login = github.getLogin();
		this.profileImageUrl = github.getImageUrl();
		this.url = github.getGithubUrl();
		this.recentCommitAt = github.getRecentCommitAt();
		this.recentCommitMessage = github.getRecentCommitMessage();
		this.location = github.getLocation();
		this.company = github.getCompany();
		this.followers = github.getFollowers();
		this.following = github.getFollowing();
	}
}

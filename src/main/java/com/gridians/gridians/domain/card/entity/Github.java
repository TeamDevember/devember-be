package com.gridians.gridians.domain.card.entity;


import com.gridians.gridians.domain.card.dto.GithubDto;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.global.entity.BaseEntity;
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

	@OneToOne(mappedBy = "github", fetch = FetchType.LAZY)
	private User user;

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
}

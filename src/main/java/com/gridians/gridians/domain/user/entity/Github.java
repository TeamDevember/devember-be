package com.gridians.gridians.domain.user.entity;


import com.gridians.gridians.domain.user.dto.GithubDto;
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

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id")
	private User user;

	private String name;
	private String login;
	private String profileImageUrl;
	private String url;
	private String recentCommitMessage;
	private String location;
	private String company;

	@Column(unique = true)
	private Long githubNumberId;
	private Long followers;
	private Long following;

	private LocalDate recentCommitAt;

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

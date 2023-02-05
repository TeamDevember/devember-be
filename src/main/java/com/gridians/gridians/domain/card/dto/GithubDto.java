package com.gridians.gridians.domain.card.dto;

import com.gridians.gridians.domain.card.entity.Github;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GithubDto {

	private String name;
	private String login;
	private Long githubId;
	private String githubUrl;
	private Long followers;
	private Long following;
	private String location;
	private String company;
	private String imageUrl;
	private LocalDate recentCommitAt;
	private String recentCommitMessage;

	@Getter
	@Setter
	public static class Request {

		private String githubId;
		private Long profileCardId;
	}

	@Builder
	@Setter
	@Getter
	public static class Response {

		private String githubName;
		private String githubAccount;

		private String githubProfileImageUrl;
		private String githubUrl;

		private LocalDate recentCommitAt;
		private String recentCommitMessage;

		private Long follower;
		private Long following;

		private String location;
		private String company;

		public static Response from(Github github) {
			return Response.builder()
					.githubName(github.getName())
					.githubAccount(github.getLogin())
					.githubProfileImageUrl(github.getProfileImageUrl())
					.recentCommitAt(github.getRecentCommitAt())
					.recentCommitMessage(github.getRecentCommitMessage())
					.follower(github.getFollowers())
					.following(github.getFollowing())
					.location(github.getLocation())
					.company(github.getCompany())
					.build();
		}
	}
}


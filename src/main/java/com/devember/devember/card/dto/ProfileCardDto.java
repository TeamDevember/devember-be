package com.devember.devember.card.dto;


import com.devember.devember.card.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ProfileCardDto {

	@Getter
	@Setter
	public static class updateRequest{
		private String status;
		private String statusMessage;
		private String field;
		private Set<String> skillSet;
		private Set<SnsDto> snsSet;
	}

	@Getter
	@Setter
	@Builder
	public static class ReadResponse {

		private String status;
		private String statusMessage;
		private String field;
		private Set<String> skillSet;
		private Set<SnsDto> snsSet;
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

		public static ReadResponse from(Detail detail, Field field, Github github, Set<Skill> skill, Set<Sns> sns) {

			Set<String> skills = new HashSet<>();
			Set<SnsDto> snss = new HashSet<>();

			for (Skill s : skill) {
				skills.add(s.getName());
			}

			for (Sns s : sns) {
				SnsDto.from(s);
			}

		return ReadResponse.builder()
				.status(detail.getStatus())
				.statusMessage(detail.getStatusMessage())
				.field(field.getName())
				.skillSet(skills)
				.snsSet(snss)
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


	@Builder
	@Setter
	@Getter
	public static class SnsDto {

		private String name;
		private String account;

		public static SnsDto from(Sns sns) {
			return SnsDto.builder()
					.name(sns.getName())
					.account(sns.getAccount())
					.build();
		}
	}

}

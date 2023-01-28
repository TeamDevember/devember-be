package com.gridianz.girdianz.domain.card.dto;


import com.gridianz.girdianz.domain.card.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ProfileCardDto {

	@Getter
	@Setter
	public static class updateRequest {

		@NotBlank
		private String statusMessage;
		@NotBlank
		private String field;
		@NotNull
		private Set<String> skillSet;
		@NotNull
		private Set<SnsDto> snsSet;
		@NotNull
		private Set<String> tagSet;

	}

	@Getter
	@Setter
	@Builder
	public static class ReadResponse {

		private String statusMessage;
		private String field;

		private Set<String> tagSet;
		private Set<String> skillSet;
		private Set<SnsDto> snsSet;

		public static ReadResponse from(String statusMessage, Field field, Set<ProfileCardSkill> profileCardSkillSet, Set<Sns> snsSet, Set<Tag> tagSet) {

			Set<String> skills = new HashSet<>();
			Set<SnsDto> snss = new HashSet<>();
			Set<String> tags = new HashSet<>();

			for (ProfileCardSkill s : profileCardSkillSet) {
				skills.add(s.getSkill().getName());
			}

			for (Sns s : snsSet) {
				snss.add(SnsDto.from(s));
			}

			for (Tag tag : tagSet) {
				tags.add(tag.getName());
			}

			return ReadResponse.builder()
					.statusMessage(statusMessage)
					.field(field.getName())
					.skillSet(skills)
					.tagSet(tags)
					.snsSet(snss)
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


	@Builder
	@Setter
	@Getter
	public static class githubResponse {

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

		public static githubResponse from(Github github) {
			return githubResponse.builder()
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

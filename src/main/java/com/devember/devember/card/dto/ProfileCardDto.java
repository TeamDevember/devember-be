package com.devember.devember.card.dto;


import com.devember.devember.card.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileCardDto {

	@Getter
	@Setter
	public static class updateRequest{
		private String statusMessage;
		private String field;
		private List<String> skillList;
		private List<SnsDto> snsList;
		private List<String> tagList;

	}

	@Getter
	@Setter
	@Builder
	public static class ReadResponse {

		private String statusMessage;
		private String field;
		private String githubName;
		private String githubAccount;

		private List<String> tagSet;
		private List<String> skillSet;
		private List<SnsDto> snsSet;

		private String githubProfileImageUrl;
		private String githubUrl;

		private LocalDate recentCommitAt;
		private String recentCommitMessage;

		private Long follower;
		private Long following;

		private String location;
		private String company;

		public static ReadResponse from(String statusMessage, Field field, Github github, List<ProfileCardSkill> profileCardSkillList, List<Sns> snsList, List<ProfileCardTag> profileCardTagList) {

			List<String> skills = new ArrayList<>();
			List<SnsDto> snss = new ArrayList<>();
			List<String> tags = new ArrayList<>();

			for (ProfileCardSkill s : profileCardSkillList) {
				skills.add(s.getSkill().getName());
			}

			for (Sns s : snsList) {
				SnsDto.from(s);
			}

			for (ProfileCardTag profileCardTag : profileCardTagList) {
				tags.add(profileCardTag.getTag().getName());
			}


		return ReadResponse.builder()
				.statusMessage(statusMessage)
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

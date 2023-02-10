package com.gridians.gridians.domain.card.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.gridians.gridians.domain.card.entity.Github;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.entity.Sns;
import com.gridians.gridians.domain.card.entity.Tag;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileCardDto {

	@Getter
	@Setter
	public static class Request {

		private String statusMessage;
		private String introduction;
		private String field;
		private String skill;
		private Set<SnsResponse> snsSet;
		private Set<String> tagSet;

	}

	@Getter
	@Setter
	@Builder
	public static class DetailResponse {

		private String statusMessage;
		private String introduction;
		private String field;
		private String skill;

		private Set<String> tagSet;
		private Set<SnsResponse> snsSet;

		private List<CommentDto.Response> commentList;
		private String imageSrc;

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

		public static DetailResponse from(ProfileCard pc, List<CommentDto.Response> commentDtoList) {

			Set<SnsResponse> snss = new HashSet<>();
			Set<String> tags = new HashSet<>();

			Set<Sns> pcSnsSet = pc.getSnsSet();
			Set<Tag> pcTagSet = pc.getTagList();

			for (Sns s : pcSnsSet) {
				snss.add(SnsResponse.from(s));
			}

			for (Tag tag : pcTagSet) {
				tags.add(tag.getName());
			}

			return DetailResponse.builder()
					.introduction(pc.getIntroduction())
					.commentList(commentDtoList)
					.statusMessage(pc.getStatusMessage() == null ? "" : pc.getStatusMessage())
					.field(pc.getField() == null ? "" : pc.getField().getName())
					.skill(pc.getSkill() == null ? "" : pc.getSkill().getName())
					.tagSet(tags == null ? new HashSet<>() : tags)
					.snsSet(snss == null ? new HashSet<>() : snss)
					.build();
		}

		public static DetailResponse from(Github github, ProfileCard pc, List<CommentDto.Response> commentDtoList) {

			Set<SnsResponse> snss = new HashSet<>();
			Set<String> tags = new HashSet<>();

			Set<Sns> pcSnsSet = pc.getSnsSet();
			Set<Tag> pcTagSet = pc.getTagList();

			for (Sns s : pcSnsSet) {
				snss.add(SnsResponse.from(s));
			}

			for (Tag tag : pcTagSet) {
				tags.add(tag.getName());
			}

			return DetailResponse.builder()
					.introduction(pc.getIntroduction())
					.commentList(commentDtoList)
					.statusMessage(pc.getStatusMessage() == null ? "" : pc.getStatusMessage())
					.field(pc.getField() == null ? "" : pc.getField().getName())
					.skill(pc.getSkill() == null ? "" : pc.getSkill().getName())
					.tagSet(tags == null ? new HashSet<>() : tags)
					.snsSet(snss == null ? new HashSet<>() : snss)
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

	@Getter
	@Setter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SimpleResponse {

		private String field;
		private String imageSrc;
		private String nickname;
		private String skillSrc;
		private Long profileCardId;

		public static SimpleResponse from(ProfileCard pc) {

			return SimpleResponse.builder()
					.field(pc.getField() == null ? "" : pc.getField().getName())
					.nickname(pc.getUser() == null ? "" : pc.getUser().getNickname())
					.profileCardId(pc.getId())
					.build();
		}
	}

	@Builder
	@Setter
	@Getter
	public static class SnsResponse {

		private String name;
		private String account;

		public static SnsResponse from(Sns sns) {
			return SnsResponse.builder()
					.name(sns.getName())
					.account(sns.getAccount())
					.build();
		}
	}

	@Builder
	@Setter
	@Getter
	public static class GithubResponse {

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

		public static GithubResponse from(Github github) {
			return GithubResponse.builder()
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

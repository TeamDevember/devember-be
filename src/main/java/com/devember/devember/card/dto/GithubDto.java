package com.devember.devember.card.dto;

import com.devember.devember.card.entity.Github;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class GithubDto {


	@Getter
	@Setter
	@Builder
	public static class Response {

		private String name;
		private String login;
		private Long id;
		private String githubUrl;
		private Long followers;
		private Long following;
		private String location;
		private String imageUrl;

		public static Response from(Github github){
			return Response.builder()
					.id(github.getId())
					.login(github.getLogin())
					.name(github.getName())
					.followers(github.getFollowersUrl())
					.following(github.getFollowingUrl())
					.location(github.getLocation())
					.imageUrl(github.getProfileImageUrl())
					.githubUrl(github.getUrl())



					.build();
		}
	}
}

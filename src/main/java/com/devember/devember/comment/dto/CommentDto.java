package com.devember.devember.comment.dto;


import com.devember.devember.comment.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

public class CommentDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateRequest {

		private String contents;

	}


	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateRequest {

		private String contents;
		private Long commentId;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteRequest {

		private Long commentId;

	}


	@Getter
	@Setter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Response  {

		private String contents;
		private LocalDate createdAt;
		private Long commentId;
		private String nickname;

		public static Response from(Comment comment){

			return Response.builder()
					.createdAt(comment.getCreatedAt().toLocalDate())
					.contents(comment.getContent())
					.commentId(comment.getId())
					.nickname(comment.getUser().getNickname())
					.build();
		}
	}
}

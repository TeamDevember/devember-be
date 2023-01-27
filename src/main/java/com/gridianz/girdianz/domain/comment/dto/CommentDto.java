package com.gridianz.girdianz.domain.comment.dto;


import com.gridianz.girdianz.domain.comment.entity.Comment;
import com.gridianz.girdianz.domain.user.entity.User;
import lombok.*;

public class CommentDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
		private User user;
		private String contents;
	}

	@Getter
	@Setter
	@Builder
	public static class Response  {

		private String contents;

		public static Response from(Comment comment){
			return Response.builder()
					.contents(comment.getContent())
					.build();
		}
	}
}

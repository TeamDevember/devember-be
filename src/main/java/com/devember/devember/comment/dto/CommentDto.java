package com.devember.devember.comment.dto;


import com.devember.devember.comment.entity.Comment;
import com.devember.devember.user.entity.User;
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

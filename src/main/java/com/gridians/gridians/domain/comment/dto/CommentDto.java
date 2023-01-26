package com.gridians.gridians.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

		private List<ReplyDto.Response> replyList;


		public static Response from(Comment comment){

			List<Reply> replies = comment.getReplyList();
			List<ReplyDto.Response> replyList = new ArrayList<>();
			for (Reply reply : replies) {
				replyList.add(ReplyDto.Response.from(reply));
			}


			return Response.builder()
					.createdAt(comment.getCreatedAt().toLocalDate())
					.contents(comment.getContent())
					.commentId(comment.getId())
					.nickname(comment.getUser().getNickname())
					.replyList(replyList)
					.build();
		}
	}
}

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
	public static class Request {

		private String contents;

	}

	@Getter
	@Setter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Response {

		private Long commentId;

		private String contents;
		private String nickname;
		private String profileImage;

		private LocalDate createdAt;

		private List<ReplyDto.Response> replyList;

		public static Response from(Comment comment) {

			List<Reply> replies = comment.getReplyList();
			List<ReplyDto.Response> replyList = new ArrayList<>();

			if (replies != null) {
				for (Reply reply : replies) {
					replyList.add(ReplyDto.Response.from(reply));
				}
			}

			return Response.builder()
					.createdAt(comment.getCreatedAt().toLocalDate())
					.contents(comment.getContents())
					.commentId(comment.getId())
					.nickname(comment.getUser().getNickname())
					.replyList(replyList)
					.build();
		}
	}
}

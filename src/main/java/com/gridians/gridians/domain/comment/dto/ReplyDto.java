package com.gridians.gridians.domain.comment.dto;


import com.gridians.gridians.domain.comment.entity.Reply;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

public class ReplyDto {

	@Getter
	@Setter

	public static class CreateRequest {

		private String contents;

	}


	@Getter
	@Setter
	public static class UpdateRequest {

		private String contents;
		private Long replyId;

	}

	@Getter
	@Setter
	public static class DeleteRequest {

		private Long replyId;

	}


	@Getter
	@Setter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Response  {

		private String contents;
		private String nickname;
		private LocalDate createdAt;
		private Long replyId;
		private Long commentId;

		public static ReplyDto.Response from(Reply reply){

			return Response.builder()
					.createdAt(reply.getCreatedAt().toLocalDate())
					.contents(reply.getContent())
					.nickname(reply.getUser().getNickname())
					.replyId(reply.getId())
					.commentId(reply.getComment().getId())
					.build();
		}
	}

}

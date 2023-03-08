package com.gridians.gridians.domain.comment.dto;


import com.gridians.gridians.domain.comment.entity.Reply;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

public class ReplyDto {

	@Getter
	@Setter

	public static class Request {

		private String contents;

	}

	@Getter
	@Setter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Response  {

		private Long replyId;
		private Long commentId;

		private String contents;
		private String nickname;
		private String imageSrc;

		private LocalDate createdAt;

		public static ReplyDto.Response from(Reply reply){

			return Response.builder()
					.createdAt(reply.getCreatedAt().toLocalDate())
					.contents(reply.getContents())
					.nickname(reply.getUser().getNickname())
					.replyId(reply.getId())
					.commentId(reply.getComment().getId())
					.build();
		}
	}

}

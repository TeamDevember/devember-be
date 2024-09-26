package com.gridians.gridians.domain.comment.entity;

import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.global.entity.BaseEntity;
import com.gridians.gridians.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	private String contents;


	public static Reply from(ReplyDto.Request reply){
		return Reply.builder()
				.contents(reply.getContents())
				.build();
	}

}

package com.devember.devember;

import com.devember.devember.comment.dto.CommentDto;
import com.devember.devember.comment.entity.Comment;
import com.devember.devember.comment.repository.CommentRepository;
import com.devember.devember.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) // 어플리케이션 컨텍스트 전부를 읽어오는 게 아닌 @Autowired가 붙거나 @MockBean에 해당하는 것들만 불러옴
@DataJpaTest //내장형 DB를 사용해서 테스트할 수 있다는 장점
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DevemberApplicationTests {

	@Autowired
	CommentService commentService;

	@Autowired
	CommentRepository commentRepository;


	@BeforeEach
	void init(){

		CommentDto.UpdateRequest updateRequest = new CommentDto.UpdateRequest();
		updateRequest.setCommentId(1L);
		updateRequest.setContents("hi");
		updateRequest.setProfileCardId(1L);
		updateRequest.setParentId(1L);

		Comment comment = Comment.from(updateRequest);
		commentRepository.save(comment);
	}

	@Test
	void update() {

		Comment findComment = commentRepository.findById(1L).orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
		findComment.setContent("hi-mama");


	}

}

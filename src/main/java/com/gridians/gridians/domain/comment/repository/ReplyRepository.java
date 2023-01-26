package com.gridians.gridians.domain.comment.repository;

import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

	List<Reply> findAllByComment_Id(Long commentId);
	List<Reply> findAllByComment(Comment comment);


}

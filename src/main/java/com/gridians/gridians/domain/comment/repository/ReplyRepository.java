package com.gridians.gridians.domain.comment.repository;

import com.gridians.gridians.domain.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	List<Reply> findAllByComment_IdOrderByCreatedAtDesc(Long commentId);
	Optional<Reply> findByComment_IdAndId(Long CommentId, Long replyId);
}

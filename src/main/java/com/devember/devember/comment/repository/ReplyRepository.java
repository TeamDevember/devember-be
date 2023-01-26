package com.devember.devember.comment.repository;

import com.devember.devember.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

	List<Reply> findAllByComment_Id(Long commentId);


}

package com.gridians.gridians.domain.comment.repository;


import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByProfileCardOrderByCreatedAtDesc(ProfileCard pc);

}

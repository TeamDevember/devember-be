package com.devember.devember.comment.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByProfileCard(ProfileCard pc);

}

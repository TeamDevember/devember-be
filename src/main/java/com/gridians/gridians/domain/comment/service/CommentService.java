package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public CommentDto.Response write(CommentDto.Request request){
		Comment comment = Comment.from(request);
		Comment savedComment = commentRepository.save(comment);
		return CommentDto.Response.from(savedComment);
	}

	public CommentDto.Response read(Long id) {
		Comment savedComment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException());

		return CommentDto.Response.from(savedComment);
	}

	public CommentDto.Response update(Long id, CommentDto.Request request) {
		Comment savedComment = commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException());
		savedComment.update(request);
		Comment newComment = commentRepository.save(savedComment);
		return CommentDto.Response.from(newComment);
	}

	public void delete(Long id) {
		commentRepository.delete(commentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException()));
	}
}

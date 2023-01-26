package com.devember.devember.comment.service;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.exception.CardException;
import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.card.type.CardErrorCode;
import com.devember.devember.comment.dto.CommentDto;
import com.devember.devember.comment.entity.Comment;
import com.devember.devember.comment.repository.CommentRepository;
import com.devember.devember.comment.repository.ReplyRepository;
import com.devember.devember.user.entity.User;
import com.devember.devember.user.exception.UserException;
import com.devember.devember.user.repository.UserRepository;
import com.devember.devember.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final ReplyRepository replyRepository;
	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;

	/**
	 * 댓글 -> 부모의 여부에 따라 댓글, 대댓글로 나뉘어짐
	 * 여부는 DTO에 값이 담겼는지에 따라
	 * request는 각각 만들까? -> 놉
	 */

	@Transactional
	public void write(Long id, CommentDto.CreateRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));



		Comment comment = Comment.from(request);
		comment.setUser(user);
		Comment savedComment = commentRepository.save(comment);
		pc.addComment(savedComment);
		profileCardRepository.save(pc);
	}

	@Transactional
	public List<CommentDto.Response> read(Long id) {
		ProfileCard pc = profileCardRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
		List<Comment> commentList = commentRepository.findAllByProfileCard(pc);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();
		for (Comment comment : commentList) {
			commentDtoList.add(CommentDto.Response.from(comment));
		}

		return commentDtoList;
	}

	@Transactional
	public void update(Long id, CommentDto.UpdateRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Comment findComment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

		if (user != findComment.getUser()) {
			throw new RuntimeException("작성자만 수정할 수 있습니다.");
		}
		findComment.setContent(request.getContents());
	}

	@Transactional
	public void delete(Long id, CommentDto.DeleteRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Comment findComment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

		if (user != findComment.getUser()) {
			throw new RuntimeException("작성자만 삭제할 수 있습니다.");
		}

		commentRepository.delete(findComment);
	}
}

package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.card.type.CardErrorCode;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CommentService {

	private final CommentRepository commentRepository;
	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;

	private final S3Service s3Service;

	/**
	 * 댓글 -> 부모의 여부에 따라 댓글, 대댓글로 나뉘어짐
	 * 여부는 DTO에 값이 담겼는지에 따라
	 * request는 각각 만들까? -> 놉
	 */

	@Transactional
	public CommentDto.Response write(Long id, CommentDto.Request request, String email) throws IOException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		ProfileCard pc = profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Comment comment = Comment.from(request);
		comment.setUser(user);
		Comment savedComment = commentRepository.save(comment);
		pc.addComment(savedComment);
		profileCardRepository.save(pc);

		CommentDto.Response response = CommentDto.Response.from(savedComment);
		response.setImageSrc(s3Service.getProfileImage(user.getId().toString()));
		return response;
	}

	public List<CommentDto.Response> read(Long profileCardId) throws IOException {
		ProfileCard pc = profileCardRepository.findById(profileCardId).orElseThrow(() -> new RuntimeException(""));
		List<Comment> commentList = commentRepository.findAllByProfileCardOrderByCreatedAtDesc(pc);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();
		for (Comment comment : commentList) {
			CommentDto.Response commentResponse = CommentDto.Response.from(comment);
			commentResponse.setImageSrc(s3Service.getProfileImage(comment.getUser().getId().toString()));
			List<Reply> replyList = comment.getReplyList();
			List<ReplyDto.Response> replyResponseList = new ArrayList<>();
			for (Reply reply : replyList) {
				ReplyDto.Response replyResponse = ReplyDto.Response.from(reply);
				replyResponse.setImageSrc(s3Service.getProfileImage(reply.getUser().getId().toString()));
				replyResponseList.add(replyResponse);
			}
			commentResponse.setReplyList(replyResponseList);
			commentDtoList.add(commentResponse);
		}

		return commentDtoList;
	}

	@Transactional
	public void update(Long profileCardId, Long commentId, CommentDto.Request request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		profileCardRepository.findById(profileCardId)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

		if (user != findComment.getUser()) {
			throw new RuntimeException("작성자만 수정할 수 있습니다.");
		}
		findComment.setContent(request.getContents());
	}

	@Transactional
	public void delete(Long id, Long commentId, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		profileCardRepository.findById(id)
				.orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

		Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

		if (user != findComment.getUser()) {
			throw new RuntimeException("작성자만 삭제할 수 있습니다.");
		}

		commentRepository.delete(findComment);
	}
}

package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.card.repository.ProfileCardRepository;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import com.gridians.gridians.domain.comment.exception.CommentException;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CommentService {

	private final CommentRepository commentRepository;
	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;

	@Value("${server.host.api}")
	private String server;

	@Value("${custom.path.profile}")
	private String profilePath;

	private String separator = "/";

	@Transactional
	public CommentDto.Response write(Long profileCardId, CommentDto.Request request, String email) {
		User findUser = verifyUserByEmail(email);
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);

		Comment comment = Comment.from(request);
		comment.setUser(findUser);
		Comment savedComment = commentRepository.save(comment);
		findProfileCard.addComment(savedComment);
		profileCardRepository.save(findProfileCard);

		CommentDto.Response response = CommentDto.Response.from(savedComment);
		response.setProfileImage(server + separator + profilePath + separator + comment.getUser().getEmail());
		return response;
	}

	public List<CommentDto.Response> read(Long profileCardId) {
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);

		List<Comment> findCommentList = commentRepository.findAllByProfileCardOrderByCreatedAtDesc(findProfileCard);
		List<CommentDto.Response> commentDtoList = new ArrayList<>();
		for (Comment comment : findCommentList) {
			CommentDto.Response commentResponse = CommentDto.Response.from(comment);
			commentResponse.setProfileImage(server + separator + profilePath + separator + comment.getUser().getEmail());
			List<Reply> replyList = comment.getReplyList();
			List<ReplyDto.Response> replyResponseList = new ArrayList<>();
			for (Reply reply : replyList) {
				ReplyDto.Response replyResponse = ReplyDto.Response.from(reply);
				replyResponse.setImageSrc(server + separator + profilePath + separator + comment.getUser().getEmail());
				replyResponseList.add(replyResponse);
			}
			commentResponse.setReplyList(replyResponseList);
			commentDtoList.add(commentResponse);
		}

		return commentDtoList;
	}

	@Transactional
	public void update(Long profileCardId, Long commentId, CommentDto.Request request, String email) {
		User user = verifyUserByEmail(email);
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);
		Comment findComment = verifyCommentById(commentId);

		if (user != findComment.getUser()) {
			throw new CommentException(ErrorCode.MODIFY_ONLY_WRITER);

		}
		findComment.setContents(request.getContents());
	}

	@Transactional
	public void delete(Long profileCardId, Long commentId, String email) {
		User findUser = verifyUserByEmail(email);
		ProfileCard findProfileCard = verifyProfileCardById(profileCardId);

		Comment findComment = verifyCommentById(commentId);

		if (findUser != findComment.getUser()) {
			throw new CommentException(ErrorCode.DELETE_ONLY_WRITER);
		}

		commentRepository.delete(findComment);
	}

	public User verifyUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
	}

	public ProfileCard verifyProfileCardById(Long profileCardId) {
		return profileCardRepository.findById(profileCardId)
				.orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
	}

	public Comment verifyCommentById(Long commentId) {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
	}
}

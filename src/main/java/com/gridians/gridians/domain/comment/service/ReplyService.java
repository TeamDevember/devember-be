package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.exception.CardException;
import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import com.gridians.gridians.domain.comment.exception.CommentException;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.comment.repository.ReplyRepository;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

	private final CommentRepository commentRepository;
	private final ReplyRepository replyRepository;
	private final UserRepository userRepository;

	@Value("${server.host.api}")
	private String serverApi;

	@Value("${custom.path.profile}")
	private String profilePath;

	private String separator = "/";

	@Transactional
	public void write(Long commentId, ReplyDto.Request request, String email) {
		User findUser = verifyUserByEmail(email);
		Comment findComment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

		Reply reply = Reply.from(request);
		reply.setUser(findUser);
		reply.setComment(findComment);
		Reply savedReply = replyRepository.save(reply);

		findComment.addReply(savedReply);
	}

	public List<ReplyDto.Response> read(Long commentId) {

		List<Reply> findReplyList = replyRepository.findAllByComment_IdOrderByCreatedAtDesc(commentId);
		List<ReplyDto.Response> replyList = new ArrayList<>();
		for (Reply reply : findReplyList) {
			ReplyDto.Response response = ReplyDto.Response.from(reply);
			response.setImageSrc(serverApi + separator + profilePath + separator + reply.getUser().getEmail());
			replyList.add(response);
		}

		return replyList;
	}

	@Transactional
	public void update(Long commentId, Long replyId, ReplyDto.Request request, String email) {
		User findUser = verifyUserByEmail(email);
		Reply findReply = verifyReplyByCommentIdAndId(commentId, replyId);

		if (findUser != findReply.getUser()) {
			throw new CommentException(ErrorCode.MODIFY_ONLY_WRITER);
		}
		findReply.setContents(request.getContents());
	}

	@Transactional
	public void delete(Long commentId, Long replyId, String email) {
		User findUser = verifyUserByEmail(email);
		Reply findReply = verifyReplyByCommentIdAndId(commentId, replyId);

		if (findUser != findReply.getUser()) {
			throw new CommentException(ErrorCode.DELETE_ONLY_WRITER);
		}
		replyRepository.delete(findReply);
	}

	public User verifyUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
	}

	public Reply verifyReplyByCommentIdAndId(Long commentId, Long replyId) {
		return replyRepository.findByComment_IdAndId(commentId, replyId)
				.orElseThrow(() -> new CommentException(ErrorCode.REPLY_NOT_FOUND));
	}

}

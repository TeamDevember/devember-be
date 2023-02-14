package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import com.gridians.gridians.domain.comment.exception.CommentException;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.comment.repository.ReplyRepository;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
	private final S3Service s3Service;

	@Transactional
	public void write(Long commentId, ReplyDto.Request request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

		Reply reply = Reply.from(request);
		reply.setUser(user);
		reply.setComment(comment);
		Reply savedReply = replyRepository.save(reply);

		comment.addReply(savedReply);
	}

//	public List<ReplyDto.Response> read(Long commentId, int page, int size) {

	public List<ReplyDto.Response> read(Long commentId) throws IOException {

//		대댓글 페이지네이션
//		PageRequest pageRequest = PageRequest.of(page, size);
//		List<Reply> findReplyList = replyRepository.findAllByComment_IdOrderByCreatedAtDesc(commentId, pageRequest);
		List<Reply> findReplyList = replyRepository.findAllByComment_IdOrderByCreatedAtDesc(commentId);
		List<ReplyDto.Response> replyList = new ArrayList<>();
		for (Reply reply : findReplyList) {
			ReplyDto.Response response = ReplyDto.Response.from(reply);
			response.setImageSrc(s3Service.getProfileImage(reply.getUser().getId().toString()));
			replyList.add(response);
		}

		return replyList;
	}

	@Transactional
	public void update(Long commentId, Long replyId, ReplyDto.Request request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Reply findReply = replyRepository.findByComment_IdAndId(commentId, replyId)
				.orElseThrow(() -> new CommentException(ErrorCode.REPLY_NOT_FOUND));

		if (user != findReply.getUser()) {
			throw new CommentException(ErrorCode.MODIFY_ONLY_WRITER);
		}
		findReply.setContent(request.getContents());
	}

	@Transactional
	public void delete(Long commentId, Long replyId, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		Reply findReply = replyRepository.findByComment_IdAndId(commentId, replyId)
				.orElseThrow(() -> new CommentException(ErrorCode.REPLY_NOT_FOUND));

		if (user != findReply.getUser()) {
			throw new CommentException(ErrorCode.DELETE_ONLY_WRITER);
		}
		replyRepository.delete(findReply);
	}
}

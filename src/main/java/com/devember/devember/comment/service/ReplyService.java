package com.devember.devember.comment.service;

import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.comment.dto.ReplyDto;
import com.devember.devember.comment.entity.Comment;
import com.devember.devember.comment.entity.Reply;
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
public class ReplyService {

	private final CommentRepository commentRepository;
	private final ReplyRepository replyRepository;
	private final ProfileCardRepository profileCardRepository;
	private final UserRepository userRepository;

	@Transactional
	public void write(Long commentId, ReplyDto.CreateRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

		Reply reply = Reply.from(request);
		reply.setUser(user);
		reply.setComment(comment);
		Reply savedReply = replyRepository.save(reply);

		comment.addReply(savedReply);
	}

	@Transactional
	public List<ReplyDto.Response> read(Long commentId) {
		List<Reply> findReplyList = replyRepository.findAllByComment_Id(commentId);
		List<ReplyDto.Response> replyList = new ArrayList<>();
		for (Reply reply : findReplyList) {
			replyList.add(ReplyDto.Response.from(reply));
		}

		return replyList;
	}

	@Transactional
	public void update(Long commentId, ReplyDto.UpdateRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
		Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));
		if (user != reply.getUser()) {
			throw new RuntimeException("작성자만 수정할 수 있습니다.");
		}
		reply.setContent(request.getContents());

	}

	@Transactional
	public void delete(Long commentId, ReplyDto.DeleteRequest request, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

		Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));

		if (user != reply.getUser()) {
			throw new RuntimeException("작성자만 삭제할 수 있습니다.");
		}
		replyRepository.delete(reply);
	}
}

package com.gridians.gridians.domain.comment.service;

import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.entity.Comment;
import com.gridians.gridians.domain.comment.entity.Reply;
import com.gridians.gridians.domain.comment.repository.CommentRepository;
import com.gridians.gridians.domain.comment.repository.ReplyRepository;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReplyService {

	private final CommentRepository commentRepository;
	private final ReplyRepository replyRepository;
	private final UserRepository userRepository;
	private final S3Service s3Service;

	@Transactional
	public void write(Long commentId, ReplyDto.Request request, String email) {
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
	public List<ReplyDto.Response> read(Long commentId, int page, int size) {

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
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Reply findReply = replyRepository.findByComment_IdAndId(commentId, replyId)
				.orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));

		if (user != findReply.getUser()) {
			throw new RuntimeException("작성자만 수정할 수 있습니다.");
		}
		findReply.setContent(request.getContents());
	}

	@Transactional
	public void delete(Long commentId, Long replyId, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Reply findReply = replyRepository.findByComment_IdAndId(commentId, replyId)
				.orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));

		if (user != findReply.getUser()) {
			throw new RuntimeException("작성자만 삭제할 수 있습니다.");
		}
		replyRepository.delete(findReply);
	}
}

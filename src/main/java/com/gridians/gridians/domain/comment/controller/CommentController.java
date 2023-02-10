package com.gridians.gridians.domain.comment.controller;

import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.service.CommentService;
import com.gridians.gridians.domain.comment.service.ReplyService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cards/{profileCardId}/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final ReplyService replyService;

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}

	@PostMapping
	public ResponseEntity<?> writeComment(@PathVariable Long profileCardId, @RequestBody CommentDto.Request request) {
		String email = getUserEmail();
		commentService.write(profileCardId, request, email);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<?> readComment(@PathVariable Long profileCardId) {
		return new ResponseEntity(commentService.read(profileCardId), HttpStatus.OK);
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<?> updateComment(@PathVariable Long profileCardId, @PathVariable Long commentId, @RequestBody CommentDto.Request request){
		String email = getUserEmail();
		commentService.update(profileCardId, commentId, request, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable Long profileCardId, @PathVariable Long commentId) {
		String email = getUserEmail();
		commentService.delete(profileCardId, commentId, email);
		return ResponseEntity.ok().build();
	}

	// 답글
	@PostMapping("/{commentId}")
	public ResponseEntity<?> writeReply(@PathVariable Long commentId, @RequestBody ReplyDto.Request request) {
		String email = getUserEmail();
		replyService.write(commentId, request, email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<?> readReply(@PathVariable Long commentId) {
		return new ResponseEntity(replyService.read(commentId), HttpStatus.OK);
	}

	@PutMapping("/{commentId}/{replyId}")
	public ResponseEntity<?> updateReply(@PathVariable Long commentId, @PathVariable Long replyId, @RequestBody ReplyDto.Request request) {
		String email = getUserEmail();
		replyService.update(commentId, replyId, request, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{commentId}/{replyId}")
	public ResponseEntity<?> deleteReply(@PathVariable Long commentId, @PathVariable Long replyId) {
		String email = getUserEmail();
		replyService.delete(commentId, replyId, email);
		return ResponseEntity.ok().build();
	}
}

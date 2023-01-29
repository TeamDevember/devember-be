package com.gridians.gridians.domain.comment.controller;


import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.domain.comment.dto.ReplyDto;
import com.gridians.gridians.domain.comment.service.CommentService;
import com.gridians.gridians.domain.comment.service.ReplyService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cards/{id}/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final ReplyService replyService;
	private final JwtUtils jwtUtils;

	// 댓글

	@PostMapping
	public ResponseEntity<?> writeComment(@PathVariable Long id, @RequestBody CommentDto.CreateRequest request, @RequestHeader(name = "Authorization") String token) {
		String email = jwtUtils.getUserEmailFromToken(token);
		commentService.write(id, request, email);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<?> readComment(@PathVariable Long id) {
		return new ResponseEntity(commentService.read(id), HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDto.UpdateRequest request, @RequestHeader(name = "Authorization") String token){
		String email = jwtUtils.getUserEmailFromToken(token);
		commentService.update(id, request, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteComment(@PathVariable Long id, @RequestBody CommentDto.DeleteRequest request, @RequestHeader(name = "Authorization") String token) {
		String email = jwtUtils.getUserEmailFromToken(token);
		commentService.delete(id, request, email);
		return ResponseEntity.ok().build();
	}

	// 답글

	@PostMapping("/{commentId}")
	public ResponseEntity<?> writeReply(@PathVariable Long commentId, @RequestBody ReplyDto.CreateRequest request, @RequestHeader(name = "Authorization") String token) {
		String email = jwtUtils.getUserEmailFromToken(token);
		replyService.write(commentId, request, email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<?> readReply(@PathVariable Long commentId) {
		return new ResponseEntity(replyService.read(commentId), HttpStatus.OK);
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<?> updateReply(@PathVariable Long commentId, @RequestBody ReplyDto.UpdateRequest request, @RequestHeader(name = "Authorization") String token){
		String email = jwtUtils.getUserEmailFromToken(token);
		replyService.update(commentId, request, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteReply(@PathVariable Long commentId, @RequestBody ReplyDto.DeleteRequest request, @RequestHeader(name = "Authorization") String token) {
		String email = jwtUtils.getUserEmailFromToken(token);
		replyService.delete(commentId, request, email);
		return ResponseEntity.ok().build();
	}

}

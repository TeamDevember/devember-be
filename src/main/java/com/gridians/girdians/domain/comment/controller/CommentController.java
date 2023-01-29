package com.gridians.girdians.domain.comment.controller;


import com.gridians.girdians.domain.comment.dto.CommentDto;
import com.gridians.girdians.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<?> write(@RequestBody CommentDto.Request request){
		commentService.write(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> read(@PathVariable Long id){
		return new ResponseEntity(commentService.read(id), HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CommentDto.Request request){
		commentService.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		commentService.delete(id);
		return ResponseEntity.ok().build();
	}
}

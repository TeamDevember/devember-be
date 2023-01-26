package com.devember.devember.comment.controller;


import com.devember.devember.comment.service.ReplyService;
import com.devember.devember.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/cards/{id}/comments/{commentId}")
@RestController
@RequiredArgsConstructor
public class ReplyController {

	private final ReplyService replyService;
	private final JwtUtils jwtUtils;


}

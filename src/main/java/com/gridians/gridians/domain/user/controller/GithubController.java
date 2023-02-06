package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.card.dto.GithubDto;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.utils.JwtUtils;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequestMapping("/user/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final UserService userService;
	private final JwtUtils jwtUtils;

	@PostMapping
	public ResponseEntity<?> registerGithub(@RequestHeader(name = "Authorization") String token, @RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		String email = jwtUtils.getUserEmailFromToken(token);

		userService.saveGithub(email, request);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> updateGithub(@RequestHeader(name = "Authorization") String token, @RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		String email = jwtUtils.getUserEmailFromToken(token);

		userService.saveGithub(email, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub(@RequestHeader(name = "Authorization") String token, @RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		String email = jwtUtils.getUserEmailFromToken(token);

		userService.deleteGithub(email, request);
		return ResponseEntity.ok().build();
	}
}

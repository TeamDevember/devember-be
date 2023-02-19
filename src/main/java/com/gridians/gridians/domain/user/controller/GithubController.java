package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.GithubDto;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/user/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final UserService userService;

	@PutMapping
	public ResponseEntity<?> updateGithub(@RequestBody GithubDto.UpdateRequest request){
		String email = getUserEmail();
		userService.updateGithub(email, request.getGithubId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub() {
		String email = getUserEmail();
		userService.deleteGithub(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

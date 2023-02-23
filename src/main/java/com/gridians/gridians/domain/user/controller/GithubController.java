package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.service.GithubService;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.Join;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GithubController {

	private final GithubService githubService;

	@Secured("ROLE_USER")
	@PostMapping("/user/github")
	public ResponseEntity<?> saveGithub(@RequestBody UserDto.GitRequest gitRequestDto) throws Exception {
		String email = getUserEmail();
		githubService.saveGithub(email, gitRequestDto.getToken());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/user/github")
	public ResponseEntity<?> updateGithub(@RequestBody JoinDto.Request request) {
		String email = getUserEmail();
		githubService.updateGithub(email, request.getGithubNumberId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/user/github")
	public ResponseEntity<?> deleteGithub() {
		githubService.deleteGithub(getUserEmail());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

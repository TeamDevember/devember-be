package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.service.GithubService;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GithubController {

	private final UserService userService;
	private final GithubService githubService;

	@PutMapping("/user/auth/github")
	public ResponseEntity<?> updateGithub(@RequestBody LoginDto.SocialRequest request) throws Exception {
		Long githubId = Long.valueOf(githubService.githubRequest(request.getToken()));
		String email = getUserEmail();
		userService.updateGithub(email, githubId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/user/github")
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

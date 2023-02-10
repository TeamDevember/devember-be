package com.gridians.gridians.domain.card.controller;

import com.gridians.gridians.domain.card.dto.GithubDto;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/cards/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> registerGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		String email = getUserEmail();
		profileCardService.saveGithub(email, request.getGithubId());
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> updateGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {
		String email = getUserEmail();
		profileCardService.saveGithub(email, request.getGithubId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub() throws IOException, ParseException, java.text.ParseException {
		String email = getUserEmail();
		profileCardService.deleteGithub(email);
		return ResponseEntity.ok().build();
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

package com.devember.devember.card.controller;

import com.devember.devember.card.service.ProfileCardService;
import com.devember.devember.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequestMapping("/cards/{id}/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final JwtUtils jwtUtils;
	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> registerGithub(@PathVariable Long id, String githubId) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithubInfo(id, githubId);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> updateGithub(@PathVariable Long id, String githubId) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithubInfo(id, githubId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub(@PathVariable Long id) throws IOException, ParseException {

		profileCardService.deleteGithub(id);
		return ResponseEntity.ok().build();
	}
}

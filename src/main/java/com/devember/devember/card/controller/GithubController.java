package com.devember.devember.card.controller;

import com.devember.devember.card.dto.GithubDto;
import com.devember.devember.card.service.ProfileCardService;
import com.devember.devember.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequestMapping("/cards/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final JwtUtils jwtUtils;
	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> registerGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithubInfo(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> updateGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithubInfo(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.removeGithub(request);
		return ResponseEntity.ok().build();
	}
}

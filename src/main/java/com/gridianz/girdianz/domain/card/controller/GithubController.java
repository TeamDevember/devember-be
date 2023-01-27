package com.gridianz.girdianz.domain.card.controller;

import com.gridianz.girdianz.domain.card.dto.GithubDto;
import com.gridianz.girdianz.domain.card.service.ProfileCardService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequestMapping("/cards/github")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> registerGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithub(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> updateGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.saveGithub(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteGithub(@RequestBody GithubDto.Request request) throws IOException, ParseException, java.text.ParseException {

		profileCardService.deleteGithub(request);
		return ResponseEntity.ok().build();
	}
}

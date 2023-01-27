package com.gridianz.girdianz.domain.card.controller;

import com.gridianz.girdianz.domain.card.dto.ProfileCardDto;
import com.gridianz.girdianz.domain.card.service.ProfileCardService;
import com.gridianz.girdianz.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cards")
@RestController
@RequiredArgsConstructor
public class ProfileCardController {

	private final JwtUtils jwtUtils;
	private final ProfileCardService profileCardService;

	@GetMapping("/{id}")
	public ResponseEntity<?> read(@PathVariable Long id) {
		return new ResponseEntity<>(profileCardService.readProfileCard(id), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestHeader(name = "Authorization") String token) {
		String email = jwtUtils.getUserEmailFromToken(token);
		profileCardService.createProfileCard(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> input(@PathVariable Long id, @RequestBody ProfileCardDto.updateRequest request) {
		profileCardService.input(id, request);
		return ResponseEntity.ok().build();
	}


	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProfileCardDto.updateRequest request) {
		profileCardService.input(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		profileCardService.deleteProfileCard(id);
		return ResponseEntity.ok().build();
	}
}
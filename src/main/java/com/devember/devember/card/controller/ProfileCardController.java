package com.devember.devember.card.controller;


import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.service.ProfileCardService;
import com.devember.devember.utils.JwtUtils;
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
		System.out.println(token);
		String email = jwtUtils.getUserEmailFromToken(token);
		System.out.println(email);
		profileCardService.createProfileCard(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> inputData(@PathVariable Long id, @RequestBody ProfileCardDto.updateRequest request) {
		profileCardService.inputData(id, request);
		return ResponseEntity.ok().build();
	}


	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProfileCardDto.updateRequest request) {
		profileCardService.inputData(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		profileCardService.deleteProfileCard(id);
		return ResponseEntity.ok().build();
	}
}
package com.gridians.gridians.domain.card.controller;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
	public ResponseEntity<?> input(@PathVariable Long id, @RequestPart MultipartFile multipartFile, @RequestPart ProfileCardDto.updateRequest request) throws IOException {
		profileCardService.input(id, request, multipartFile);
		return ResponseEntity.ok().build();
	}


	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestPart MultipartFile multipartFile, @RequestPart ProfileCardDto.updateRequest request) throws IOException {
		profileCardService.input(id, request, multipartFile);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		profileCardService.deleteProfileCard(id);
		return ResponseEntity.ok().build();
	}
}
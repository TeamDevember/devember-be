package com.gridians.gridians.domain.card.controller;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RequestMapping("/cards")
@RestController
@RequiredArgsConstructor
public class ProfileCardController {

	private final ProfileCardService profileCardService;
	private final S3Service s3Service;

	@GetMapping("/{id}")
	public ResponseEntity<?> read(@PathVariable Long id) {
		String email = getUserEmail();
		return new ResponseEntity<>(profileCardService.readProfileCard(email, id), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> cardList(int page, int size) {
		log.info("CardList Read");
		return new ResponseEntity<>(profileCardService.allProfileCardList(page, size), HttpStatus.OK);
	}

	@GetMapping("/favorites")
	public ResponseEntity<?> favoriteCardList(int page, int size) {
		String email = getUserEmail();
		log.info("Favorite CardList Read");
		return new ResponseEntity<>(profileCardService.favoriteCardList(email, page, size), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> create() {
		String email = getUserEmail();
		ProfileCard pc = profileCardService.createProfileCard(email);
		log.info("[" + pc.getUser().getNickname() + "] Create Profile Card");
		return ResponseEntity.ok().build();
	}


	@PostMapping("/{id}")
	public ResponseEntity<?> input(@PathVariable Long id, @RequestBody @Valid ProfileCardDto.Request request) throws IOException {
		String email = getUserEmail();
		profileCardService.input(email, id, request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProfileCardDto.Request request) throws IOException {
		String email = getUserEmail();
		profileCardService.input(email, id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ProfileCard pc = profileCardService.deleteProfileCard(id);
		log.info("[" + pc.getUser().getNickname() + "] Delete Profile Card");
		return ResponseEntity.ok().build();
	}

	@GetMapping("/images/skills/{skill}")
	public ResponseEntity<?> getImage(@PathVariable String skill) throws IOException {
		// 실제 주소가 되어야 함
		return new ResponseEntity(s3Service.getSkillImage(skill), HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}
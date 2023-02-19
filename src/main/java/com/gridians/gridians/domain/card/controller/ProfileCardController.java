package com.gridians.gridians.domain.card.controller;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.card.service.ProfileCardService;
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

	@GetMapping("/my-card")
	public ResponseEntity<?> getMyCard(){
		String email = getUserEmail();
		return new ResponseEntity<>(profileCardService.getMyCard(email), HttpStatus.OK);
	}

	@GetMapping("/{profileCardId}")
	public ResponseEntity<?> read(@PathVariable Long profileCardId) {
		return new ResponseEntity<>(profileCardService.readProfileCard(profileCardId), HttpStatus.OK);
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
		return new ResponseEntity(HttpStatus.OK);
	}

	@PutMapping("/{profileCardId}")
	public ResponseEntity<?> update(@PathVariable Long profileCardId, @RequestBody @Valid ProfileCardDto.Request request) throws IOException {
		String email = getUserEmail();
		profileCardService.input(email, profileCardId, request);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("/{profileCardId}")
	public ResponseEntity<?> delete(@PathVariable Long profileCardId) {
		String email = getUserEmail();
		ProfileCard pc = profileCardService.deleteProfileCard(email, profileCardId);
		log.info("[" + pc.getUser().getNickname() + "] Delete Profile Card");
		return ResponseEntity.ok().build();
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
//
//	@GetMapping("/dummy")
//	public void dummy(){
//		profileCardService.dummy();
//	}
}
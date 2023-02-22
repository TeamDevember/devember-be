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
		return new ResponseEntity<>(profileCardService.getMyCard(getUserEmail()), HttpStatus.OK);
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
		log.info("Favorite CardList Read");
		return new ResponseEntity<>(profileCardService.favoriteCardList(getUserEmail(), page, size), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> create() {
		ProfileCard pc = profileCardService.createProfileCard(getUserEmail());
		log.info("[" + pc.getUser().getNickname() + "] Create Profile Card");
		return new ResponseEntity(HttpStatus.OK);
	}

	@PutMapping("/{profileCardId}")
	public ResponseEntity<?> update(@PathVariable Long profileCardId, @RequestBody @Valid ProfileCardDto.Request request) throws IOException {
		ProfileCard pc = profileCardService.input(getUserEmail(), profileCardId, request);
		log.info("[" + pc.getUser().getNickname() + "] Update Profile Card");

		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<?> delete() {
		ProfileCard pc = profileCardService.deleteProfileCard(getUserEmail());
		log.info("[" + pc.getUser().getNickname() + "] Delete Profile Card");
		return ResponseEntity.ok().build();
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}
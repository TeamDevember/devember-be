package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.card.dto.ProfileCardDto;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.domain.user.dto.FavoriteDto;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/fav")
@Controller
public class FavoriteController {

	private final UserService userService;

	@Secured("ROLE_USER")
	@PostMapping
	public ResponseEntity<?> create(
			@RequestBody FavoriteDto.Request favoriteDto
	) {
		String email = getUserEmail();
		userService.addFavorite(email, favoriteDto.getProfileCardId());
		return ResponseEntity.ok().build();
	}

	@Secured("ROLE_USER")
	@DeleteMapping
	public ResponseEntity<?> delete(
			@RequestBody FavoriteDto.Request favoriteDto
	) {
		String userEmail = getUserEmail();

		userService.deleteFavorite(userEmail, favoriteDto.getProfileCardId());
		return ResponseEntity.ok().build();
	}

	@Secured("ROLE_USER")
	@GetMapping
	public ResponseEntity<?> read() throws IOException {
		String email = getUserEmail();
		return new ResponseEntity<>(userService.favoriteList(email), HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

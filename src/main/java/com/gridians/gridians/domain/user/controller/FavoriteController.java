package com.gridians.gridians.domain.user.controller;

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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/fav")
@RestController
public class FavoriteController {

	private final UserService userService;

	@PostMapping
	@Secured("ROLE_USER")
	public ResponseEntity<?> create(
			@RequestBody FavoriteDto.Request favoriteDto
	) {
		String email = getUserEmail();
		userService.addFavorite(email, favoriteDto.getEmail());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@Secured("ROLE_USER")
	public ResponseEntity<?> delete(
			@RequestBody FavoriteDto.Request favoriteDto
	) {
		String userEmail = getUserEmail();
		String email = favoriteDto.getEmail();

		userService.deleteFavorite(userEmail, email);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@Secured("ROLE_USER")
	public ResponseEntity<?> read(int page, int size) throws IOException {
		String email = getUserEmail();
		return new ResponseEntity<>(userService.favoriteList(email, page, size), HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

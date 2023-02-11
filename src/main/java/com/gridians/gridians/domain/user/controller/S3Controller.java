package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.service.S3Service;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

	private final S3Service s3Service;

    @Secured("ROLE_USER")
	@PutMapping("/user/profile")
	public void upload(MultipartFile multipartFile) throws IOException {
		String email = getUserEmail();
		s3Service.upload(email, multipartFile);
	}

	@GetMapping("/imageUrl/{id}")
	public ResponseEntity<?> getImage(@PathVariable String id) throws IOException {
		return new ResponseEntity<>(s3Service.getProfileImage(id), HttpStatus.OK);
	}

	private String getUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
		return userDetails.getEmail();
	}
}

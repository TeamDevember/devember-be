package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.ImageDto;
import com.gridians.gridians.domain.user.service.ImageService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/profile-images/{email}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String email) {
        return new ResponseEntity<>(imageService.getProfileImage(email),HttpStatus.OK);
    }

    @GetMapping(value = "/skill-images/{skill}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getProfileCardSkillImage(@PathVariable String skill) {
        return new ResponseEntity<>(imageService.getSkillImage(skill), HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @PutMapping("/user/profile")
    public ResponseEntity updateProfileImage(@RequestBody ImageDto imageDto) {

        String userEmail = getUserEmail();
        imageService.updateProfileImage(userEmail, imageDto.getBase64Image());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/user/profile")
    public ResponseEntity deleteProfileImage() {

        String userEmail = getUserEmail();
        imageService.deleteProfileImage(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getEmail();
    }
}

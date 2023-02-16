package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.ImageDto;
import com.gridians.gridians.domain.user.service.ImageService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @ResponseBody
    @GetMapping(value = "/profile-image/{email}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getProfileImage(@PathVariable String email) {
        log.info("호출");
        return imageService.getProfileImage(email);
    }

    @ResponseBody
    @GetMapping(value = "/skill-image/{skill}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getProfileCardSkillImage(@PathVariable String skill) {
        log.info("호출");

        return imageService.getSkillImage(skill);
    }


    @Secured("ROLE_USER")
    @PutMapping("/user/profile")
    public ResponseEntity updateProfileImage(@RequestBody ImageDto imageDto) {
        log.info("호출");

        String userEmail = getUserEmail();
        imageService.updateProfileImage(userEmail, imageDto.getBase64Image());

        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/user/profile")
    public ResponseEntity deleteProfileImage() {
        log.info("호출");


        String userEmail = getUserEmail();
        imageService.deleteProfileImage(userEmail);

        return ResponseEntity.ok().build();
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getEmail();
    }
}

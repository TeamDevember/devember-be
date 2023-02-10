package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.PhotoDto;
import com.gridians.gridians.domain.user.service.PhotoService;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @ResponseBody
    @GetMapping(value = "/image/{email}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getUserProfile(
            @PathVariable String email
    ) {
        return photoService.getProfileImage(email);
    }

    @Secured("ROLE_USER")
    @PutMapping("/user/profile")
    public ResponseEntity updateProfileImage(
            @RequestBody PhotoDto photoDto
    ) {
        String userEmail = getUserEmail();
        photoService.updateProfileImage(userEmail, photoDto.getBase64Image());

        return ResponseEntity.ok().build();
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getEmail();
    }
}

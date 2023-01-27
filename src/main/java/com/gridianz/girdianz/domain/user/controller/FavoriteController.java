package com.gridianz.girdianz.domain.user.controller;

import com.gridianz.girdianz.global.config.security.userdetail.JwtUserDetails;
import com.gridianz.girdianz.domain.user.dto.FavoriteDto;
import com.gridianz.girdianz.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/fav")
@Controller
public class FavoriteController {

    private final UserService userService;

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<?> create(
            @RequestBody FavoriteDto.Request favoriteDto
    ) {
        String userEmail = getUserEmail();
        String email = favoriteDto.getEmail();

        userService.addFavorite(userEmail, email);
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

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getEmail();
    }
}

package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.domain.user.dto.FavoriteDto;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/fav")
@RestController
public class FavoriteController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<?> create(@RequestHeader(name = "Authorization") String token,
            @RequestBody FavoriteDto.Request favoriteDto
    ) {
        String userEmail = jwtUtils.getUserEmailFromToken(token);
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
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getEmail();
    }
}

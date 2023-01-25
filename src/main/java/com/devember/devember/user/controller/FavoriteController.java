package com.devember.devember.user.controller;

import com.devember.devember.card.repository.ProfileCardRepository;
import com.devember.devember.config.security.userdetail.JwtUserDetails;
import com.devember.devember.user.dto.FavoriteDto;
import com.devember.devember.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        JwtUserDetails user = getUser();
        String email = favoriteDto.getEmail();
        userService.addFavorite(user.getEmail(), email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Secured("ROLE_USER")
    public ResponseEntity<?> delete(
            @RequestBody FavoriteDto.Request favoriteDto
    ) {
        JwtUserDetails user = getUser();
        String email = favoriteDto.getEmail();
        userService.deleteFavorite(user.getEmail(), email);
        return ResponseEntity.ok().build();
    }

    private JwtUserDetails getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUserDetails) authentication.getPrincipal();
    }
}

package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.dto.UserDto;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.gridians.global.utils.CookieUtils;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody JoinDto.Request request) throws Exception {
        User user = userService.signUp(request);

        return new ResponseEntity(JoinDto.Response.from(user), HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(
            @RequestBody LoginDto.Request loginDto,
            HttpServletResponse response
    ) {
        userService.verifyUser(loginDto.getEmail(), loginDto.getPassword());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        LoginDto.Response res = userService.login(authentication);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/exist")
    public ResponseEntity<?> existUserByEmail(String email) {
        return new ResponseEntity<>(userService.checkUser(email), HttpStatus.OK);
    }

    @PostMapping("/auth/social-login")
    public ResponseEntity socialLogin(
            @RequestBody LoginDto.SocialRequest loginDto
    ) throws Exception {
        Authentication authentication = userService.socialLogin(loginDto.getToken());
        LoginDto.Response res = userService.login(authentication);

        return ResponseEntity.ok().body(res);
    }

    private String generateToken(HttpServletResponse response, Authentication authentication) {
        String accessToken = userService.createAccessToken(authentication);
        String refreshToken = userService.createRefreshToken(authentication);

        CookieUtils.addHttpOnlyCookie(response, "re-token", refreshToken, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());
        return accessToken;
    }

    @GetMapping("/auth/email-auth")
    public ResponseEntity<?> auth(String id) {
        return new ResponseEntity(userService.joinAuth(id), HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @GetMapping("/valid")
    public ResponseEntity getUser() throws IOException {
        String userEmail = getUserEmail();
        UserDto.Response userInfo = userService.getUserInfo(userEmail);
        return new ResponseEntity(userInfo, HttpStatus.OK);
    }

    @PostMapping("/auth/find-password")
    public ResponseEntity findPassword(
            @RequestBody UserDto.Request userDto
    ) {
        userService.findPassword(userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/update-email")
    public ResponseEntity sendEmailVerify(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.sendUpdateEmail(userEmail, userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/update-user")
    public ResponseEntity updateUser(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.updateUser(userEmail, userDto);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/update-email")
    public ResponseEntity updateEmail(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.updateEmail(userEmail, userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(
            @RequestBody UserDto.deleteRequest userDto
    ) {
        String userEmail = getUserEmail();
        userService.deleteUser(userEmail, userDto.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity reissue(
            @RequestBody UserDto.RequestToken req
    ) {
        String issueAccessToken = userService.issueAccessToken(req.getRefreshToken());
        return ResponseEntity.ok().body(UserDto.ResponseToken.from(issueAccessToken));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/logout")
    public ResponseEntity logout(
            @RequestBody UserDto.RequestToken req
    ) {
        userService.logout(req.getAccessToken(), req.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getEmail();
    }
}

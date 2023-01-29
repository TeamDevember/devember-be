package com.gridians.girdians.domain.user.controller;

import com.gridians.girdians.domain.user.dto.JoinDto;
import com.gridians.girdians.domain.user.dto.UserDto;
import com.gridians.girdians.domain.user.entity.User;
import com.gridians.girdians.domain.user.service.UserService;
import com.gridians.girdians.global.config.security.userdetail.JwtUserDetails;
import com.gridians.girdians.domain.user.dto.LoginDto;
import com.gridians.girdians.global.utils.CookieUtils;
import com.gridians.girdians.global.utils.JwtUtils;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody JoinDto.Request request) {
        User user = userService.signUp(request);

        return new ResponseEntity(JoinDto.Response.from(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(
            @RequestBody LoginDto.Request loginDto,
            HttpServletResponse response
    ) {
        userService.verifyUser(loginDto.getEmail(), loginDto.getPassword());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String accessToken = generateToken(response, authentication);
        User user = ((JwtUserDetails) authentication.getPrincipal()).getUser();

        return ResponseEntity.ok().body(LoginDto.Response.from(accessToken, user.getNickname(), user.getEmail(), loginDto.getPassword().length()));
    }

    @PostMapping("/exist")
    public ResponseEntity<?> existUserByEmail(String email) {
        return new ResponseEntity<>(userService.checkUser(email), HttpStatus.OK);
    }

    @PostMapping("/social-login")
    public ResponseEntity socialLogin(
            HttpServletResponse response,
            @RequestBody LoginDto.SocialRequest loginDto
    ) throws Exception {
        Authentication authentication = userService.socialLogin(loginDto.getToken());
        String accessToken = generateToken(response, authentication);

        User user = ((JwtUserDetails) authentication.getPrincipal()).getUser();

        return ResponseEntity.ok().body(LoginDto.Response.socialFrom(accessToken));
    }

    @GetMapping("/email-auth")
    public ResponseEntity<?> auth(String id) {
        return new ResponseEntity(userService.joinAuth(id), HttpStatus.OK);
    }

    private String generateToken(HttpServletResponse response, Authentication authentication) {
        String accessToken = userService.createAccessToken(authentication);
        String refreshToken = userService.createRefreshToken(authentication);

        CookieUtils.addCookie(response, "re-token", refreshToken, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());
//		String reCookie = "re-token=" + refreshToken;
//		response.addHeader("Set-Cookie", reCookie +"; Secure; SameSite=None");
        return accessToken;
    }

    @Secured("ROLE_USER")
    @GetMapping("find-password")
    public ResponseEntity findPassword() {
        String email = getUserEmail();
        userService.findPassword(email);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("update-email")
    public ResponseEntity sendEmailVerify(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.sendUpdateEmail(userEmail, userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("update-user")
    public ResponseEntity  updateUser(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.updateUser(userEmail, userDto);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PutMapping("update-email")
    public ResponseEntity updateEmail(
            @RequestBody UserDto.Request userDto
    ) {
        String userEmail = getUserEmail();
        userService.verifyUserPassword(userEmail, userDto.getPassword());
        userService.updateEmail(userEmail, userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("ROLE_USER")
    @DeleteMapping("/delete")
    public ResponseEntity deleteUser() {
        String userEmail = getUserEmail();
        userService.deleteUser(userEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity reissue(
            @RequestHeader(value = "AUTH-TOKEN") String accessToken,
            @RequestHeader(value = "re-token")
            String refreshToken
    ) {
        String issueAccessToken = userService.issueAccessToken(refreshToken);

        return null;
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(
            @RequestHeader(value = "AUTH-TOKEN") String accessToken,
            @RequestHeader(value = "re-token") String refreshToken,
            HttpServletResponse response
    ) {
        userService.logout(accessToken, refreshToken);

        CookieUtils.addHttpOnlyCookie(response, "re-token", "", 0);
        return ResponseEntity.ok().build();
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getEmail();
    }
}

package com.gridians.gridians.domain.user.controller;

import com.gridians.gridians.domain.user.dto.JoinDto;
import com.gridians.gridians.domain.user.dto.LoginDto;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.service.UserService;
import com.gridians.gridians.global.utils.CookieUtils;
import com.gridians.gridians.global.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtUtils jwtUtils;
	private final AuthenticationManager authenticationManager;

	// Get요청은 처음과 나중에 다르게 적용해도 됨
	@GetMapping("/images/{id}")
	public ResponseEntity<Resource> getImage(@PathVariable String id) throws IOException {
		// 실제 주소가 되어야 함
		String file = "/Users/j/j/images/" + id + ".png";
		Path path = new File(file).toPath();
		FileSystemResource resource = new FileSystemResource(path);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(path))).body(resource);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody JoinDto.Request request) {
		User user = userService.signUp(request);

		if(user == null){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		return new ResponseEntity(JoinDto.Response.from(user), HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity login(
			@RequestBody LoginDto.Request loginDto,
			HttpServletResponse response
	) {
		int flag = userService.verifyUser(loginDto.getEmail(), loginDto.getPassword());
		if(flag != 3) {
			if(flag == 1){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else if(flag == 2) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

		Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		String accessToken = generateToken(response, authentication);

		LoginDto.Response res = LoginDto.Response.builder()
				.token(accessToken)
				.build();

		return ResponseEntity.ok().body(res);
	}

	@PostMapping("/exist")
	public ResponseEntity<?> existUserByEmail(String email){
		return new ResponseEntity<>(userService.checkUser(email), HttpStatus.OK);
	}

//	@PostMapping("/social-login")
//	public ResponseEntity socialLogin(
//			HttpServletResponse response,
//			@RequestBody LoginDto.SocialRequest loginDto
//	) throws Exception {
//		log.info("status = {}", loginDto.getStatus());
//		log.info("token = {}", loginDto.getToken());
//		Authentication authentication = userService.socialLogin(loginDto.getToken(), loginDto.getStatus());
//
//		String accessToken = generateToken(response, authentication);
//
//		LoginDto.Response res = LoginDto.Response.builder()
//				.token(accessToken)
//				.build();
//
//		return ResponseEntity.ok().body(res);
//	}

	@GetMapping("/email-auth")
	public ResponseEntity<?> auth(String id) {
		return new ResponseEntity(userService.joinAuth(id), HttpStatus.OK);
	}


	private String generateToken(HttpServletResponse response, Authentication authentication){
		String accessToken = userService.createAccessToken(authentication);
		String refreshToken = userService.createRefreshToken(authentication);

		CookieUtils.addCookie(response, "re-token", refreshToken, jwtUtils.REFRESH_TOKEN_EXPIRE_TIME.intValue());
//		String reCookie = "re-token=" + refreshToken;
//		response.addHeader("Set-Cookie", reCookie +"; Secure; SameSite=None");
		return accessToken;
	}

	@PostMapping("find-email")
	public ResponseEntity findEmail() {

		return null;
	}

	@PostMapping("find-password")
	public ResponseEntity findPassword() {

		return null;
	}

	@PostMapping("/reissue")
	public ResponseEntity reissue(
			@RequestHeader(value = "AUTH-TOKEN") String accessToken,
			@RequestHeader(value = "re-token") String refreshToken
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
}

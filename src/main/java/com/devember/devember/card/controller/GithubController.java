package com.devember.devember.card.controller;

import com.devember.devember.card.service.ProfileCardService;
import com.devember.devember.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequestMapping("/cards/{profileCardId}/githubs")
@RestController
@RequiredArgsConstructor
public class GithubController {

	private final JwtUtils jwtUtils;
	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> saveGithub(@PathVariable Long profileCardId,
	                                    @PathVariable String id) throws IOException, ParseException {

		profileCardService.saveGithubInfo(profileCardId, id);
		return ResponseEntity.ok().build();
	}
}

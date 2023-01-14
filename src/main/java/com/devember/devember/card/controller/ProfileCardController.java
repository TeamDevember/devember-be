package com.devember.devember.card.controller;


import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.service.ProfileCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cards")
@RestController
@RequiredArgsConstructor
public class ProfileCardController {

	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> create(@RequestBody ProfileCardDto.createCard request){
		profileCardService.createProfileCard(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/snss")
	public ResponseEntity<?> snsUpdate(@RequestBody ProfileCardDto.snsUpdate request){
		profileCardService.snsUpdate(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/skills")
	public ResponseEntity<?> skillUpdate(@RequestBody ProfileCardDto.skillUpdate request){
		profileCardService.skillUpdate(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/fields")
	public ResponseEntity<?> fieldUpdate(@RequestBody ProfileCardDto.fieldUpdate request){
		profileCardService.fieldUpdate(request);
		return ResponseEntity.ok().build();
	}



}

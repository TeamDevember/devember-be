package com.devember.devember.card.controller;


import com.devember.devember.card.dto.ProfileCardDto;
import com.devember.devember.card.service.ProfileCardService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/cards")
@RestController
@RequiredArgsConstructor
public class ProfileCardController {

	private final ProfileCardService profileCardService;

	@PostMapping
	public ResponseEntity<?> create(@RequestBody ProfileCardDto.CardRequest request){
		profileCardService.createProfileCard(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> read(@PathVariable Long id){
		return new ResponseEntity<>(profileCardService.read(id), HttpStatus.OK);
	}

	@PostMapping("/snss")
	public ResponseEntity<?> addSns(@RequestBody ProfileCardDto.SnsRequest request){
		profileCardService.addSns(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/skills")
	public ResponseEntity<?> addSkill(@RequestBody ProfileCardDto.SkillRequest request){
		profileCardService.addSkill(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/fields")
	public ResponseEntity<?> addField(@RequestBody ProfileCardDto.FieldRequest request){
		profileCardService.addField(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/snss")
	public ResponseEntity<?> updateSns(@RequestBody ProfileCardDto.SnsRequest request){
		profileCardService.addSns(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/skills")
	public ResponseEntity<?> updateSkill(@RequestBody ProfileCardDto.SkillRequest request){
		profileCardService.addSkill(request);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/fields")
	public ResponseEntity<?> updateField(@RequestBody ProfileCardDto.FieldRequest request){
		profileCardService.addField(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/details")
	public ResponseEntity<?> addDetail(@RequestBody ProfileCardDto.DetailRequest request){
		profileCardService.addDetail(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/snss")
	public ResponseEntity<?> deleteSns(@RequestBody ProfileCardDto.DeleteSns request){
		profileCardService.deleteSns(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/skills")
	public ResponseEntity<?> deleteSns(@RequestBody ProfileCardDto.DeleteSkill request){
		profileCardService.deleteSkill(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/fields")
	public ResponseEntity<?> deleteSns(@RequestBody ProfileCardDto.DeleteField request){
		profileCardService.deleteField(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/github/{id}")
	public ResponseEntity<?> saveGithub(@PathVariable String id) throws IOException, ParseException {
		profileCardService.saveGithubInfo(id);
		return ResponseEntity.ok().build();
	}
}

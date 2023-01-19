//package com.devember.devember.card.controller;
//
//import com.devember.devember.card.dto.ProfileCardDto;
//import com.devember.devember.card.service.ProfileCardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RequestMapping("/cards/{profileCardId}/skills")
//@RestController
//@RequiredArgsConstructor
//public class SkillController {
//
//	private final ProfileCardService profileCardService;
//
//	@PostMapping("/skills")
//	public ResponseEntity<?> addSkill(@PathVariable Long profileCardId,
//	                                  @RequestBody ProfileCardDto.SkillRequest request){
//		profileCardService.addSkill(request);
//		return ResponseEntity.ok().build();
//	}
//
//	@PutMapping("/skills")
//	public ResponseEntity<?> updateSkill(@PathVariable Long profileCardId,
//	                                     @RequestBody ProfileCardDto.SkillRequest request){
//		profileCardService.addSkill(request);
//		return ResponseEntity.ok().build();
//	}
//
//	@DeleteMapping("/skills")
//	public ResponseEntity<?> deleteSkill(@PathVariable Long profileCardId,
//	                                     @RequestBody ProfileCardDto.DeleteSkill request){
//		profileCardService.deleteSkill(request);
//		return ResponseEntity.ok().build();
//	}
//
//}

//package com.devember.devember.card.controller;
//
//
//import com.devember.devember.card.dto.ProfileCardDto;
//import com.devember.devember.card.service.ProfileCardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping("/cards/{profileCardId}/snss")
//@RestController
//@RequiredArgsConstructor
//public class SnsController {
//
//	private final ProfileCardService profileCardService;
//
//	@PostMapping
//	public ResponseEntity<?> addSns(@PathVariable Long profileCardId,
//	                                @RequestBody ProfileCardDto.SnsRequest request){
//		profileCardService.addSns(profileCardId, request);
//		return ResponseEntity.ok().build();
//	}
//
//	@DeleteMapping
//	public ResponseEntity<?> deleteSns(@PathVariable Long profileCardId,
//	                                   @RequestBody ProfileCardDto.DeleteSns request){
//		profileCardService.deleteSns(profileCardId, request);
//		return ResponseEntity.ok().build();
//	}
//
//	@PutMapping
//	public ResponseEntity<?> updateSns(@PathVariable Long profileCardId,
//	                                   @RequestBody ProfileCardDto.SnsRequest request){
//		profileCardService.addSns(profileCardId, request);
//		return ResponseEntity.ok().build();
//	}
//}

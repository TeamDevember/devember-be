//package com.devember.devember.card.controller;
//
//import com.devember.devember.card.dto.ProfileCardDto;
//import com.devember.devember.card.service.ProfileCardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping("/cards/{profileCardId}/fields")
//@RestController
//@RequiredArgsConstructor
//public class FieldController {
//
//	private final ProfileCardService profileCardService;
//
//	@PostMapping
//	public ResponseEntity<?> addField(@PathVariable Long profileCardId,
//	                                  @RequestBody ProfileCardDto.FieldRequest request){
//		profileCardService.addField(request);
//		return ResponseEntity.ok().build();
//	}
//
//	@DeleteMapping
//	public ResponseEntity<?> deleteField(@PathVariable Long profileCardId,
//	                                     @RequestBody ProfileCardDto.DeleteField request){
//		profileCardService.deleteField(request);
//		return ResponseEntity.ok().build();
//	}
//
//	@PutMapping
//	public ResponseEntity<?> updateField(@PathVariable Long profileCardId,
//	                                     @RequestBody ProfileCardDto.FieldRequest request){
//		profileCardService.addField(request);
//		return ResponseEntity.ok().build();
//	}
//
//}

//package com.devember.devember.card.controller;
//
//import com.devember.devember.card.dto.ProfileCardDto;
//import com.devember.devember.card.service.ProfileCardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequestMapping("/cards/{profileCardId}/details")
//@RestController
//@RequiredArgsConstructor
//public class DetailController {
//
//	private final ProfileCardService profileCardService;
//
//	@PostMapping
//	public ResponseEntity<?> addDetail(@RequestBody ProfileCardDto.DetailRequest request){
//		profileCardService.addDetail(request);
//		return ResponseEntity.ok().build();
//	}
//
//}

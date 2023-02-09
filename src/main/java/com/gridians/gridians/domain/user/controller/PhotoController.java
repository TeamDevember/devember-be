//package com.gridians.gridians.domain.user.controller;
//
//import com.gridians.gridians.domain.user.service.PhotoService;
//import com.gridians.gridians.global.config.security.userdetail.JwtUserDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Controller
//@RequiredArgsConstructor
//public class PhotoController {
//
//    private final PhotoService photoService;
//
//    @Secured("ROLE_USER")
//    @PutMapping("/user/profile")
//    public void upload(MultipartFile multipartFile) throws IOException {
//        String email = getUserEmail();
//        photoService.updateProfileImage(email, multipartFile);
//    }
//
//    @ResponseBody
//    @GetMapping("/imageUrl/{email}")
//    public ResponseEntity<?> getImage(@PathVariable String email){
//        return new ResponseEntity<>(photoService.getImage(email), HttpStatus.OK);
//    }
//
//    private String getUserEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
//        return jwtUserDetails.getEmail();
//    }
//
////    @ResponseBody
////    @GetMapping(value = "/image/{email}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
////    public byte[] getUserProfile(
////            @PathVariable String email
////    ) {
////        return photoService.z(email);
////    }
////
////    @Secured("ROLE_USER")
////    @PutMapping("/user/profile")
////    public ResponseEntity updateProfileImage(
////            @RequestBody PhotoDto photoDto
////    ) {
////        String userEmail = getUserEmail();
////        photoService.updateProfileImage(userEmail, photoDto.getBase64Image());
////
////        return ResponseEntity.ok().build();
////    }
//}

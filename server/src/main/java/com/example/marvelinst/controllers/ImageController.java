package com.example.marvelinst.controllers;

import com.example.marvelinst.entity.Image;
import com.example.marvelinst.payload.response.MessageResponse;
import com.example.marvelinst.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageController {
    @Autowired
    ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile multipartFile,
                                                             Principal principal) throws IOException {
        imageService.uploadImageProfile(multipartFile, principal);
        return new ResponseEntity<>(new MessageResponse("Image uploaded to profile"), HttpStatus.OK);
    }
    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable String postId,
                                                             @RequestParam("file") MultipartFile multipartFile,
                                                             Principal principal) throws IOException{
        imageService.uploadImageForPost(multipartFile, principal, Long.parseLong(postId));
        return new ResponseEntity<>(new MessageResponse("Image uploaded to post"), HttpStatus.OK);
    }
    @GetMapping("/profileImage")
    public ResponseEntity<Image> getImageForUser(Principal principal) {
        Image userImage = imageService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }
    @GetMapping("/{postId}/image")
    public ResponseEntity<Image> getImageToPost(@PathVariable("postId") String postId) {
        Image postImage = imageService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }

}

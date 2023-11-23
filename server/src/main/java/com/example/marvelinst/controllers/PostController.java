package com.example.marvelinst.controllers;

import com.example.marvelinst.DTOs.PostDto;
import com.example.marvelinst.DTOs.UserDto;
import com.example.marvelinst.entity.Post;
import com.example.marvelinst.facade.PostFacade;
import com.example.marvelinst.payload.response.MessageResponse;
import com.example.marvelinst.services.PostService;
import com.example.marvelinst.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    PostFacade postFacade;
    @Autowired
    ResponseErrorValidation responseErrorValidation;
    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDto postDto, BindingResult bindingResult, Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        Post post = postService.createPost(postDto, principal);
        PostDto createdPost = postFacade.postToPostDto(post);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPosts(){
        List<PostDto> postDtoList = postService.getAllPosts()
                .stream()
                .map(postFacade::postToPostDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }
    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDto>> getAllPostOfUser(Principal principal){
        List<PostDto> postDtoList = postService.getAllPostsByUser(principal)
                .stream()
                .map(postFacade::postToPostDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }
    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDto> likePost (@PathVariable("postId") String postId,
                                             @PathVariable("username") String username){
        Post post = postService.likePost(Long.parseLong(postId), username);
        PostDto postDto = postFacade.postToPostDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }
    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable String postId, Principal principal){
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseEntity<>(new MessageResponse("Post has been deleted"), HttpStatus.OK);
    }
}

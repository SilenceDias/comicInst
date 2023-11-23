package com.example.marvelinst.controllers;

import com.example.marvelinst.DTOs.CommentDto;
import com.example.marvelinst.DTOs.PostDto;
import com.example.marvelinst.entity.Comment;
import com.example.marvelinst.facade.CommentFacade;
import com.example.marvelinst.payload.response.MessageResponse;
import com.example.marvelinst.services.CommentService;
import com.example.marvelinst.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentFacade commentFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable String postId, BindingResult bindingResult,
                                                Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        Comment comment = commentService.saveComment(Long.parseLong(postId), commentDto, principal);
        CommentDto createdComment = commentFacade.commentToCommentDto(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable String postId){
        List<CommentDto> allComments = commentService.getAllCommentForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }
    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> delete(@PathVariable String commentId){
        commentService.deleteComment(Long.parseLong(commentId));
        return new ResponseEntity<>(new MessageResponse("Comment has been deleted"), HttpStatus.OK);
    }
}

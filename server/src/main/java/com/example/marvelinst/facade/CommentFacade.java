package com.example.marvelinst.facade;

import com.example.marvelinst.DTOs.CommentDto;
import com.example.marvelinst.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {
    public CommentDto commentToCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUsername(comment.getUsername());
        commentDto.setMessage(comment.getMessage());
        return commentDto;
    }
}

package com.example.marvelinst.facade;

import com.example.marvelinst.DTOs.PostDto;
import com.example.marvelinst.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDto postToPostDto(Post post){
        PostDto postDto = new PostDto();
        postDto.setUsername(post.getUser().getUsername());
        postDto.setId(post.getId());
        postDto.setCaption(post.getCaption());
        postDto.setLikes(post.getLikes());
        postDto.setLocation(post.getLocation());
        postDto.setTitle(post.getTitle());
        postDto.setUsersLiked(post.getLikedUsers());
        return postDto;
    }
}

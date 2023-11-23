package com.example.marvelinst.services;

import com.example.marvelinst.DTOs.CommentDto;
import com.example.marvelinst.entity.Comment;
import com.example.marvelinst.entity.Post;
import com.example.marvelinst.entity.User;
import com.example.marvelinst.exceptions.PostNotFoundException;
import com.example.marvelinst.repos.CommentRepository;
import com.example.marvelinst.repos.PostRepository;
import com.example.marvelinst.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment saveComment(Long postId, CommentDto commentDto, Principal principal){
        User user = getUserPrincipal(principal);
        Post post =postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
        Comment comment = new Comment();
        comment.setMessage(commentDto.getMessage());
        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        LOG.info("Saving comment for post with id: "+ postId);
        return commentRepository.save(comment);
    }
    public List<Comment> getAllCommentForPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
        return commentRepository.findAllByPost(post);
    }
    public void deleteComment(Long id){
        Optional<Comment> comment = commentRepository.findById(id);
        comment.ifPresent(commentRepository::delete);
    }
    private User getUserPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}

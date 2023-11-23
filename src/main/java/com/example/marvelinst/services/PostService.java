package com.example.marvelinst.services;

import com.example.marvelinst.DTOs.PostDto;
import com.example.marvelinst.entity.Image;
import com.example.marvelinst.entity.Post;
import com.example.marvelinst.entity.User;
import com.example.marvelinst.exceptions.PostNotFoundException;
import com.example.marvelinst.repos.ImageRepository;
import com.example.marvelinst.repos.PostRepository;
import com.example.marvelinst.repos.UserRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class PostService {
    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Post createPost(PostDto postDto, Principal principal){
        User user = getUserPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDto.getCaption());
        post.setTitle(postDto.getTitle());
        post.setLocation(postDto.getLocation());
        post.setLikes(0);

        LOG.info("Saving post for user {}", user.getEmail());
        return postRepository.save(post);
    }
    public List<Post> getAllPosts(){
        return postRepository.findAllByOrderByCreatedDateDesc();
    }
    public Post getPostById(Long id, Principal principal){
        User user = getUserPrincipal(principal);
        return postRepository.findPostByIdAndUser(id, user).orElseThrow(() -> new PostNotFoundException("Post cannot be found for user: " + user.getEmail()));
    }
    public List<Post> getAllPostsByUser(Principal principal){
        User user = getUserPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }
    public Post likePost(Long id, String username){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " cannot be found"));
        Optional<String> userLiked = post.getLikedUsers().stream().filter(u -> u.equals(username)).findAny();
        if(userLiked.isPresent()){
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        }
        else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepository.save(post);
    }
    public void deletePost(Long id, Principal principal){
        Post post = getPostById(id, principal);
        Optional<Image> image = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        image.ifPresent(imageRepository::delete);
    }
    private User getUserPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}

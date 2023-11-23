package com.example.marvelinst.services;

import com.example.marvelinst.entity.Image;
import com.example.marvelinst.entity.Post;
import com.example.marvelinst.entity.User;
import com.example.marvelinst.exceptions.ImageNotFoundException;
import com.example.marvelinst.repos.ImageRepository;
import com.example.marvelinst.repos.PostRepository;
import com.example.marvelinst.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    @Autowired
    public ImageService(ImageRepository imageRepository, UserRepository userRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    public Image uploadImageProfile(MultipartFile multipartFile, Principal principal) throws IOException{
        User user = getUserPrincipal(principal);
        LOG.info("Uploading image profile to user: "+ user.getUsername());
        Image userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }
        Image image = new Image();
        image.setUserId(user.getId());
        image.setImageBytes(compressBytes(multipartFile.getBytes()));
        image.setName(multipartFile.getOriginalFilename());
        return imageRepository.save(image);
    }
    public Image uploadImageForPost(MultipartFile multipartFile, Principal principal, Long postId) throws IOException{
        User user = getUserPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSingleCollector());
        Image image = new Image();
        image.setPostId(post.getId());
        image.setImageBytes(compressBytes(multipartFile.getBytes()));
        image.setName(multipartFile.getOriginalFilename());
        LOG.info("Uploading image to post with id: "+ post.getId());
        return imageRepository.save(image);

    }
    public Image getImageToUser(Principal principal){
        User user = getUserPrincipal(principal);
        Image image = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(image)){
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }
        return image;
    }
    public Image getImageToPost(Long postId){
        Image image = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Cannot find image for post: "+postId));
        if(!ObjectUtils.isEmpty(image)){
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }
        return image;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            LOG.error("Cannot compress Bytes");
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            LOG.error("Cannot decompress Bytes");
        }
        return outputStream.toByteArray();
    }

    private User getUserPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
    private <T> Collector<T, ?, T> toSingleCollector(){
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if(list.size() != 1)
                        throw new IllegalStateException();
                    return list.get(0);
                }
        );
    }
}

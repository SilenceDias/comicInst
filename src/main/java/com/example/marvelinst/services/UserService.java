package com.example.marvelinst.services;


import com.example.marvelinst.DTOs.UserDto;
import com.example.marvelinst.entity.User;
import com.example.marvelinst.entity.enums.ERole;
import com.example.marvelinst.exceptions.UserExistException;
import com.example.marvelinst.payload.request.RegisterRequest;
import com.example.marvelinst.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User createUser(RegisterRequest registerRequest){
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getFirstName());
        user.setLastname(registerRequest.getLastName());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
        user.getRole().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving user {}", registerRequest.getEmail());
            return userRepository.save(user);
        }catch (Exception ex){
            LOG.error("Error during registration. {}" + ex.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exists");
        }
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(UserDto userDto, Principal principal){
        User user = getUserPrincipal(principal);
        user.setName(userDto.getName());
        user.setLastname(userDto.getLastName());
        user.setBio(userDto.getBio());
        return userRepository.save(user);
    }
    public User getUserById(Long userId){
        return userRepository.findUserById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public User getCurrentUser(Principal principal){
        return getUserPrincipal(principal);
    }
    private User getUserPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}

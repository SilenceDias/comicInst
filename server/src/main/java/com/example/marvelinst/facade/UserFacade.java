package com.example.marvelinst.facade;

import com.example.marvelinst.DTOs.UserDto;
import com.example.marvelinst.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public UserDto userToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setLastName(user.getLastname());
        userDto.setBio(user.getBio());
        userDto.setUsername(user.getUsername());
        return userDto;
    }
}

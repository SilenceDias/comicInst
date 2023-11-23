package com.example.marvelinst.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;
    private String bio;
    private String username;
}

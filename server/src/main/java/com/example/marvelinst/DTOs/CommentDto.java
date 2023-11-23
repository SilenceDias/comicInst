package com.example.marvelinst.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    @NotEmpty
    private String message;
    private String username;
}

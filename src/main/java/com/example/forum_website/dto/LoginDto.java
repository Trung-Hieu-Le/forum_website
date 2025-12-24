package com.example.forum_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "{login.username.notBlank}")
    @Size(min = 3, max = 20, message = "{login.username.size}")
    private String username;
    
    @NotBlank(message = "{login.password.notBlank}")
    @Size(min = 6, message = "{login.password.size}")
    private String password;
}

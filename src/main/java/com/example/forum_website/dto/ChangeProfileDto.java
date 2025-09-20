package com.example.forum_website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProfileDto {
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "{changeProfile.username.invalid}")
    private String username;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "{changeProfile.phone.invalid}")
    private String phone;
    
    @Email(message = "{changeProfile.email.invalid}")
    private String email;
    
    private String avatar;
    
    private String fullname;
}

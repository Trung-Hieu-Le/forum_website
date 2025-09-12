package com.example.forum_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
    @NotBlank(message = "{resetPassword.token.notBlank}")
    private String token;
    
    @NotBlank(message = "{resetPassword.newPassword.notBlank}")
    @Size(min = 6, message = "{resetPassword.newPassword.size}")
    private String newPassword;
    
    @NotBlank(message = "{resetPassword.confirmPassword.notBlank}")
    @Size(min = 6, message = "{resetPassword.confirmPassword.size}")
    private String confirmPassword;
}

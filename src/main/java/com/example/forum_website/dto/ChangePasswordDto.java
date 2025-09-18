package com.example.forum_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    @NotBlank(message = "{changePassword.currentPassword.notBlank}")
    @Size(min = 6, message = "{changePassword.currentPassword.size}")
    private String currentPassword;
    
    @NotBlank(message = "{changePassword.newPassword.notBlank}")
    @Size(min = 6, message = "{changePassword.newPassword.size}")
    private String newPassword;
    
    @NotBlank(message = "{changePassword.confirmPassword.notBlank}")
    @Size(min = 6, message = "{changePassword.confirmPassword.size}")
    private String confirmPassword;

    public ChangePasswordDto() {}

    public ChangePasswordDto(String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}

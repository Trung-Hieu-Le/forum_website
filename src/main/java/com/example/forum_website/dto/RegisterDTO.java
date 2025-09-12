package com.example.forum_website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    @NotBlank(message = "{register.username.notBlank}")
    @Size(min = 3, max = 20, message = "{register.username.size}")
    private String username;

    @NotBlank(message = "{register.email.notBlank}")
    @Email(message = "{register.email.invalid}")
    private String email;

    @NotBlank(message = "{register.password.notBlank}")
    @Size(min = 6, message = "{register.password.size}")
    private String password;

    @NotBlank(message = "{register.confirmPassword.notBlank}")
    @Size(min = 6, message = "{register.confirmPassword.size}")
    private String confirmPassword;
}

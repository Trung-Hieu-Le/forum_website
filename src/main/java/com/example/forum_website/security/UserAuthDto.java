package com.example.forum_website.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthDto {
    private Long id;
    private String username;
    private String role;
    private String fullname;
    private String avatar;
    private String email;
    private String phone;
}

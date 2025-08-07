package com.example.forum_website.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProfileDto {
    private String phone;
    private String email;
    private String avatar;
}

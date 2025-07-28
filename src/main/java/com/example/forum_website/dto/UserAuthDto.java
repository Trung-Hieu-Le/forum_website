package com.example.forum_website.dto;

public class UserAuthDto {
    private String username;
    private String avatar;

    public UserAuthDto(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }
}

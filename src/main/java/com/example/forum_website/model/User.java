package com.example.forum_website.model;

import com.example.forum_website.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private String resetToken;
    private String avatar;
    private String phone;
    private boolean emailNewPost = true;
    private boolean emailReply = true;
    private boolean emailMention = true;
    private boolean browserNotifications = false;

    public User() {}

    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

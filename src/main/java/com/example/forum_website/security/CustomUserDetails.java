package com.example.forum_website.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long id;
    private final String role;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, String role) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}

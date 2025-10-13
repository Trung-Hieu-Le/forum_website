package com.example.forum_website.constant;

public final class SecurityConstants {
    
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // URL Patterns
    public static final String[] PUBLIC_URLS = {
        "/",
        "/home",
        "/login",
        "/register",
        "/forgot-password",
        "/reset-password",
        "/error",
        "/change-language",
        "/change-theme",
        "/css/**",
        "/js/**",
        "/images/**",
        "/avatar/**"
    };
    
    public static final String[] AUTHENTICATED_URLS = {
        "/profile/**",
        "/settings/**",
        "/api/settings/**",
        "/home2"
    };
    
    public static final String[] ADMIN_URLS = {
        "/admin/**",
        "/home3"
    };
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}


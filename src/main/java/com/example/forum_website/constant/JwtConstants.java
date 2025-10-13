package com.example.forum_website.constant;

public final class JwtConstants {
    
    private JwtConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Cookies
    public static final String TOKEN_COOKIE_NAME = "tokenAuth";
    public static final String USERNAME_COOKIE_NAME = "usernameAuth";
    public static final String COOKIE_PATH = "/";
    
    // JWT Claims
    public static final String CLAIM_USER_ID = "id";
}


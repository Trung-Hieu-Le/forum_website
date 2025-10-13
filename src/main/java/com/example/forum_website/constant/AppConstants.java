package com.example.forum_website.constant;

public final class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Pagination
    public static final int DEFAULT_THREAD_PAGE_SIZE = 10;
    
    // Locale
    public static final String DEFAULT_LOCALE = "vi";
    public static final String MESSAGES_BASENAME = "messages/messages";
    public static final String LOCALE_COOKIE_NAME = "locale";
    public static final int LOCALE_COOKIE_MAX_AGE = 365 * 24 * 60 * 60; // 1 year
    
    // Avatar
    public static final String DEFAULT_AVATAR_UPLOAD_DIR = "src/main/resources/static/avatar";
    public static final String DEFAULT_AVATAR_FILENAME = "default-avatar.png";
    public static final long MAX_AVATAR_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    // Supported values
    public static final String[] SUPPORTED_LANGUAGES = {"vi", "en", "ja"};
    public static final String[] SUPPORTED_THEMES = {"light", "dark", "darkblue"};
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", 
        "image/png", 
        "image/gif", 
        "image/webp"
    };
}


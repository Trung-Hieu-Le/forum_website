package com.example.forum_website.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.forum_website.dto.UserAuthDto;
import com.example.forum_website.model.User;
import com.example.forum_website.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CurrentUserControllerAdvice {
    private static final Set<String> AUTH_CLEAR_PATHS = Set.of("/login", "/register", "/forgot-password");

    @Autowired
    private UserService userService;
    
    @ModelAttribute("userId")
    public String getCurrentUserId(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (AUTH_CLEAR_PATHS.contains(path)) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        String userId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    return userId;
                }
            }
        }
        return null;
    }

    @ModelAttribute("userAuth")
    public UserAuthDto getUserAuth(@ModelAttribute("userId") String userId) {
        if (userId != null) {
            try {
                User user = userService.getUserById(Long.parseLong(userId));
                return new UserAuthDto(user.getUsername(), user.getAvatar());
            } catch (Exception ignored) {}
        }
        return null;
    }
}

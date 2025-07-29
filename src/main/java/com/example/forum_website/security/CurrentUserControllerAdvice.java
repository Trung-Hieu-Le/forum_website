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
    
    @ModelAttribute("usernameAuth")
    public String getUsernameAuth(HttpServletRequest request) {
        if (AUTH_CLEAR_PATHS.contains(request.getRequestURI())) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("usernameAuth".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @ModelAttribute("userAuth")
    public UserAuthDto getUserAuth(@ModelAttribute("usernameAuth") String username) {
        if (username != null) {
            try {
                User user = userService.getUserByUsername(username);
                return new UserAuthDto(user.getUsername(), user.getAvatar());
            } catch (Exception ignored) {}
        }
        return null;
    }
}

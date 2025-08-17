package com.example.forum_website.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.forum_website.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CurrentUserControllerAdvice {
    private static final Set<String> AUTH_CLEAR_PATHS = Set.of("/login", "/register", "/forgot-password");

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("userAuth")
    public UserAuthDto getUserAuth(HttpServletRequest request) {
        if (AUTH_CLEAR_PATHS.contains(request.getRequestURI())) {
            return null;
        }

        String token = getTokenFromCookies(request.getCookies());
        if (token != null) {
            Claims claims = jwtUtil.validateToken(token);
            if (claims != null) {
                Long id = claims.get("id", Long.class);
                return userRepository.findById(id)
                        .map(user -> new UserAuthDto(
                                user.getId(),
                                user.getUsername(),
                                user.getRole().name(),
                                user.getFullname(),
                                user.getAvatar()))
                        .orElse(null);
            }
        }
        return null;
    }

    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("tokenAuth".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

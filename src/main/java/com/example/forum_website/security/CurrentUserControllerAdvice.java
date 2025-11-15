package com.example.forum_website.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.forum_website.constant.JwtConstants;
import com.example.forum_website.constant.SecurityConstants;
import com.example.forum_website.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("userAuth")
    public UserAuthDto getUserAuth(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        for (String path : SecurityConstants.AUTH_CLEAR_PATHS) {
            if (requestUri.equals(path)) {
                return null;
            }
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
                                user.getAvatar(),
                                user.getEmail(),
                                user.getPhone()))
                        .orElse(null);
            }
        }
        return null;
    }

    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtConstants.TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

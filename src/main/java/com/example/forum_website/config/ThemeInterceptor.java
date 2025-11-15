package com.example.forum_website.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ThemeInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // If session already has theme, skip
        if (request.getSession(false) != null && request.getSession().getAttribute("theme") != null) {
            return true;
        }
        
        // Try to load theme from cookie
        String theme = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("theme".equals(cookie.getName())) {
                    theme = cookie.getValue();
                    break;
                }
            }
        }
        
        // Set theme in session if found
        if (theme != null) {
            var session = request.getSession(true);
            session.setAttribute("theme", theme);
        }
        
        return true;
    }
}



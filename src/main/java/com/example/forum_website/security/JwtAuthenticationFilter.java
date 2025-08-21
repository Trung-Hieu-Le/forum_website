package com.example.forum_website.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.forum_website.model.User;
import com.example.forum_website.repository.UserRepository;

/**
 * JWT Authentication Filter for processing JWT tokens from cookies
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String TOKEN_COOKIE_NAME = "tokenAuth";
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        try {
            String token = getTokenFromCookies(request.getCookies());
            
            if (token != null && !token.trim().isEmpty()) {
                processJwtToken(token);
            }
            
        } catch (Exception e) {
            logger.error("Error processing JWT token in filter", e);
            // Continue with the filter chain even if JWT processing fails
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Process JWT token and set authentication if valid
     * 
     * @param token JWT token string
     */
    private void processJwtToken(String token) {
        try {
            // Check if token is expired
            if (jwtUtil.isTokenExpired(token)) {
                logger.debug("JWT token is expired");
                return;
            }
            
            // Extract user ID from token
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.debug("Could not extract user ID from JWT token");
                return;
            }
            
            // Find user and set authentication
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                setAuthentication(user);
                logger.debug("Authentication set for user: {}", user.getUsername());
            } else {
                logger.warn("User not found for JWT token with ID: {}", userId);
            }
            
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
        }
    }

    /**
     * Set authentication in SecurityContext
     * 
     * @param user authenticated user
     */
    private void setAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                user.getId(),
                user.getRole().name()
        );
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Extract JWT token from cookies
     * 
     * @param cookies array of cookies from request
     * @return JWT token string or null if not found
     */
    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        
        for (Cookie cookie : cookies) {
            if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String value = cookie.getValue();
                return (value != null && !value.trim().isEmpty()) ? value : null;
            }
        }
        
        return null;
    }

    /**
     * Check if the request should be filtered
     * 
     * @param request HTTP request
     * @return true if request should be filtered
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filtering for static resources and public endpoints
        return path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/images/") ||
               path.startsWith("/favicon.ico") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.equals("/forgot-password") ||
               path.equals("/reset-password");
    }
}
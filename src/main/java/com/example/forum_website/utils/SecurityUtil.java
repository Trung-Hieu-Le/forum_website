package com.example.forum_website.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for security operations
 */
public class SecurityUtil {

    /**
     * Get current authenticated user details
     * 
     * @return UserDetails if authenticated, null otherwise
     */
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get current authenticated username
     * 
     * @return username if authenticated, null otherwise
     */
    public static String getCurrentUsername() {
        UserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * Check if user is authenticated
     * 
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Check if user has specific role
     * 
     * @param role role to check (e.g., "ADMIN", "USER")
     * @return true if user has role, false otherwise
     */
    public static boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.toUpperCase()));
    }

    /**
     * Check if user has any of the specified roles
     * 
     * @param roles roles to check
     * @return true if user has any of the roles, false otherwise
     */
    public static boolean hasAnyRole(String... roles) {
        if (!isAuthenticated()) {
            return false;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        for (String role : roles) {
            if (userRoles.contains("ROLE_" + role.toUpperCase())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get all roles of current user
     * 
     * @return list of roles without "ROLE_" prefix
     */
    public static List<String> getCurrentUserRoles() {
        if (!isAuthenticated()) {
            return List.of();
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    /**
     * Check if current user is admin
     * 
     * @return true if user is admin, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is moderator
     * 
     * @return true if user is moderator, false otherwise
     */
    public static boolean isModerator() {
        return hasRole("MODERATOR");
    }

    /**
     * Check if current user is regular user
     * 
     * @return true if user is regular user, false otherwise
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Clear current authentication
     */
    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Get authentication object
     * 
     * @return current Authentication object
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Check if user has permission to access resource
     * 
     * @param resourceOwnerId ID of resource owner
     * @return true if user can access resource, false otherwise
     */
    public static boolean canAccessResource(Long resourceOwnerId) {
        if (!isAuthenticated()) {
            return false;
        }
        
        // Admin can access everything
        if (isAdmin()) {
            return true;
        }
        
        // User can access their own resources
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails instanceof com.example.forum_website.security.CustomUserDetails) {
            com.example.forum_website.security.CustomUserDetails customUserDetails = 
                (com.example.forum_website.security.CustomUserDetails) userDetails;
            return customUserDetails.getId().equals(resourceOwnerId);
        }
        
        return false;
    }
} 
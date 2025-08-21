package com.example.forum_website.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Utility class for handling validation operations
 */
public class ValidationUtil {

    /**
     * Extract error messages from BindingResult
     * 
     * @param result BindingResult from validation
     * @return List of error messages
     */
    public static List<String> extractErrorMessages(BindingResult result) {
        return result.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    /**
     * Check if email format is valid
     * 
     * @param email email to validate
     * @return true if email is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Check if password meets minimum requirements
     * 
     * @param password password to validate
     * @param minLength minimum length required
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password, int minLength) {
        if (password == null || password.length() < minLength) {
            return false;
        }
        
        // Check for at least one uppercase, one lowercase, and one digit
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasUppercase && hasLowercase && hasDigit;
    }

    /**
     * Check if username meets requirements
     * 
     * @param username username to validate
     * @param minLength minimum length required
     * @param maxLength maximum length allowed
     * @return true if username meets requirements
     */
    public static boolean isValidUsername(String username, int minLength, int maxLength) {
        if (username == null || username.length() < minLength || username.length() > maxLength) {
            return false;
        }
        
        // Username should only contain alphanumeric characters and underscores
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Check if string is not null or blank
     * 
     * @param value string to check
     * @return true if string is not null or blank
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if string length is within range
     * 
     * @param value string to check
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if string length is within range
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Sanitize input string (remove extra whitespace, trim)
     * 
     * @param input input string
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim().replaceAll("\\s+", " ");
    }
} 
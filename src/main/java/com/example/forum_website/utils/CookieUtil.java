package com.example.forum_website.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for handling cookie operations
 */
public class CookieUtil {

    /**
     * Create a new cookie with common settings
     * 
     * @param name cookie name
     * @param value cookie value
     * @param maxAge cookie max age in seconds
     * @return configured cookie
     */
    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        
        return cookie;
    }

    /**
     * Create a session cookie (expires when browser closes)
     * 
     * @param name cookie name
     * @param value cookie value
     * @return configured cookie
     */
    public static Cookie createSessionCookie(String name, String value) {
        return createCookie(name, value, -1);
    }

    /**
     * Create a persistent cookie with specified age
     * 
     * @param name cookie name
     * @param value cookie value
     * @param maxAge max age in seconds
     * @return configured cookie
     */
    public static Cookie createPersistentCookie(String name, String value, int maxAge) {
        return createCookie(name, value, maxAge);
    }

    /**
     * Delete a cookie by setting maxAge to 0
     * 
     * @param name cookie name to delete
     * @return cookie configured for deletion
     */
    public static Cookie deleteCookie(String name) {
        return createCookie(name, null, 0);
    }

    /**
     * Get cookie value by name from request
     * 
     * @param request HTTP request
     * @param name cookie name
     * @return cookie value or null if not found
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Check if cookie exists in request
     * 
     * @param request HTTP request
     * @param name cookie name
     * @return true if cookie exists
     */
    public static boolean hasCookie(HttpServletRequest request, String name) {
        return getCookieValue(request, name) != null;
    }

    /**
     * Add cookie to response
     * 
     * @param response HTTP response
     * @param cookie cookie to add
     */
    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    /**
     * Clear all cookies by name
     * 
     * @param response HTTP response
     * @param cookieNames array of cookie names to clear
     */
    public static void clearCookies(HttpServletResponse response, String... cookieNames) {
        for (String name : cookieNames) {
            Cookie cookie = deleteCookie(name);
            addCookie(response, cookie);
        }
    }

    /**
     * Set cookie with domain restriction
     * 
     * @param name cookie name
     * @param value cookie value
     * @param maxAge max age in seconds
     * @param domain domain restriction
     * @return configured cookie
     */
    public static Cookie createCookieWithDomain(String name, String value, int maxAge, String domain) {
        Cookie cookie = createCookie(name, value, maxAge);
        cookie.setDomain(domain);
        return cookie;
    }
} 
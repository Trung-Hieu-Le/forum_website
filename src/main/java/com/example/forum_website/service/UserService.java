package com.example.forum_website.service;

import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.model.User;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void authenticateAndSetToken(LoginDto loginDto, HttpServletResponse response) throws Exception;
    void registerUser(RegisterDto registerDto) throws Exception;
    String initiatePasswordReset(String email) throws Exception;
    void resetPassword(String token, String newPassword, String confirmPassword) throws Exception;
    User getUserById(Long userId) throws Exception;
    User getUserByUsername(String username) throws Exception;
}

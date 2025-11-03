package com.example.forum_website.service;

import java.util.Map;

import com.example.forum_website.dto.ChangePasswordDto;
import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.model.User;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void authenticateAndSetToken(LoginDto loginDto, HttpServletResponse response);
    void registerUser(RegisterDto registerDto);
    String initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword, String confirmPassword);
    User getUserById(Long userId);
    User getUserByUsername(String username);
    void updateProfile(ChangeProfileDto changeProfileDto);
    void changePassword(ChangePasswordDto changePasswordDto);
    void updateNotificationSettings(Map<String, Object> notificationSettings);
    void updateAvatar(String filename);
    String getCurrentUserAvatar();
    User getCurrentUser();
}

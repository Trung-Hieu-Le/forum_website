package com.example.forum_website.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.forum_website.dto.ChangePasswordDto;
import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.enums.UserRole;
import com.example.forum_website.model.User;
import com.example.forum_website.repository.UserRepository;
import com.example.forum_website.security.JwtUtil;
import com.example.forum_website.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void authenticateAndSetToken(LoginDto loginDto, HttpServletResponse response) throws Exception {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("auth.userNotFound"));
            String token = jwtUtil.generateToken(user.getId());
            Cookie cookie = new Cookie("tokenAuth", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge((int) (jwtUtil.getExpiration() / 1000));
            response.addCookie(cookie);
        } catch (BadCredentialsException e) {
            throw new Exception("auth.invalid");
        } catch (LockedException e) {
            throw new Exception("auth.locked");
        } catch (AuthenticationException e) {
            throw new Exception("auth.failed");
        }
    }

    @Override
    public void registerUser(RegisterDto registerDto) throws Exception {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new Exception("register.passwordsNotMatch");
        }
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new Exception("register.username.exists");
        }
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new Exception("register.email.exists");
        }
        User user = new User(
                registerDto.getUsername(),
                registerDto.getEmail(),
                passwordEncoder.encode(registerDto.getPassword()),
                UserRole.USER);
        userRepository.save(user);
    }

    @Override
    public String initiatePasswordReset(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("forgotPassword.emailNotFound," + email));
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        return resetToken;
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) throws Exception {
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("resetPassword.passwordsNotMatch");
        }
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new Exception("resetPassword.invalidToken"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("user.notFoundById," + userId));
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("user.notFoundByUsername," + username));
    }

    @Override
    public void updateProfile(ChangeProfileDto changeProfileDto) throws Exception {
        User currentUser = getCurrentUserInternal();
        
        if (changeProfileDto.getUsername() != null && !changeProfileDto.getUsername().isEmpty()) {
            // Check if username is already taken by another user
            if (userRepository.findByUsername(changeProfileDto.getUsername()).isPresent() && 
                !currentUser.getUsername().equals(changeProfileDto.getUsername())) {
                throw new Exception("register.username.exists");
            }
            currentUser.setUsername(changeProfileDto.getUsername());
        }
        
        if (changeProfileDto.getEmail() != null && !changeProfileDto.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            if (userRepository.findByEmail(changeProfileDto.getEmail()).isPresent() && 
                !currentUser.getEmail().equals(changeProfileDto.getEmail())) {
                throw new Exception("register.email.exists");
            }
            currentUser.setEmail(changeProfileDto.getEmail());
        }
        
        if (changeProfileDto.getPhone() != null) {
            currentUser.setPhone(changeProfileDto.getPhone());
        }
        
        if (changeProfileDto.getAvatar() != null) {
            currentUser.setAvatar(changeProfileDto.getAvatar());
        }
        
        if (changeProfileDto.getFullname() != null) {
            currentUser.setFullname(changeProfileDto.getFullname());
        }
        
        userRepository.save(currentUser);
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) throws Exception {
        User currentUser = getCurrentUserInternal();
        
        // Verify current password
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new Exception("auth.invalid");
        }
        
        // Check if new password matches confirmation
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new Exception("register.passwordsNotMatch");
        }
        
        // Update password
        currentUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    public void updateNotificationSettings(Map<String, Object> notificationSettings) throws Exception {
        User currentUser = getCurrentUserInternal();
        
        // Update notification preferences
        // This is a simplified implementation - in a real app you might have a separate NotificationSettings entity
        currentUser.setEmailNewPost((Boolean) notificationSettings.getOrDefault("emailNewPost", false));
        currentUser.setEmailReply((Boolean) notificationSettings.getOrDefault("emailReply", false));
        currentUser.setEmailMention((Boolean) notificationSettings.getOrDefault("emailMention", false));
        currentUser.setBrowserNotifications((Boolean) notificationSettings.getOrDefault("browserNotifications", false));
        
        userRepository.save(currentUser);
    }

    @Override
    public void updateAvatar(String filename) throws Exception {
        User currentUser = getCurrentUserInternal();
        currentUser.setAvatar(filename);
        userRepository.save(currentUser);
    }

    @Override
    public String getCurrentUserAvatar() throws Exception {
        User currentUser = getCurrentUserInternal();
        return currentUser.getAvatar();
    }

    @Override
    public User getCurrentUser() throws Exception {
        return getCurrentUserInternal();
    }

    private User getCurrentUserInternal() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("auth.failed");
        }
        
        String username = authentication.getName();
        return getUserByUsername(username);
    }
}

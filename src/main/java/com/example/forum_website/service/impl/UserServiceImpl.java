package com.example.forum_website.service.impl;

import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.model.User;
import com.example.forum_website.enums.UserRole;
import com.example.forum_website.repository.UserRepository;
import com.example.forum_website.security.JwtUtil;
import com.example.forum_website.service.UserService;
import com.example.forum_website.utils.CookieUtil;
import com.example.forum_website.utils.ValidationUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of UserService with enhanced error handling and validation
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
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
            // Validate input
            validateLoginInput(loginDto);
            
            // Authenticate user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("auth.userNotFound"));
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId());
            
            // Set authentication cookie
            setAuthCookie(response, token);
            
            logger.info("User authenticated successfully: {}", user.getUsername());
            
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for user: {}", loginDto.getUsername());
            throw new Exception("auth.invalid");
        } catch (LockedException e) {
            logger.warn("Locked account attempt: {}", loginDto.getUsername());
            throw new Exception("auth.locked");
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", loginDto.getUsername());
            throw new Exception("auth.failed");
        } catch (Exception e) {
            logger.error("Error during authentication for user: {}", loginDto.getUsername(), e);
            throw e;
        }
    }

    @Override
    public void registerUser(RegisterDto registerDto) throws Exception {
        try {
            // Validate input
            validateRegistrationInput(registerDto);
            
            // Check if username exists
            if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
                logger.warn("Registration attempt with existing username: {}", registerDto.getUsername());
                throw new Exception("register.username.exists");
            }
            
            // Check if email exists
            if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
                logger.warn("Registration attempt with existing email: {}", registerDto.getEmail());
                throw new Exception("register.email.exists");
            }
            
            // Create and save user
            User user = new User(
                    registerDto.getUsername(),
                    registerDto.getEmail(),
                    passwordEncoder.encode(registerDto.getPassword()),
                    UserRole.USER);
            
            userRepository.save(user);
            logger.info("User registered successfully: {}", user.getUsername());
            
        } catch (Exception e) {
            logger.error("Error during user registration: {}", registerDto.getUsername(), e);
            throw e;
        }
    }

    @Override
    public String initiatePasswordReset(String email) throws Exception {
        try {
            // Validate email
            if (!ValidationUtil.isValidEmail(email)) {
                throw new Exception("forgotPassword.invalidEmail");
            }
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("forgotPassword.emailNotFound," + email));
            
            // Generate reset token
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userRepository.save(user);
            
            logger.info("Password reset initiated for user: {}", user.getUsername());
            return resetToken;
            
        } catch (Exception e) {
            logger.error("Error initiating password reset for email: {}", email, e);
            throw e;
        }
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) throws Exception {
        try {
            // Validate inputs
            validatePasswordResetInput(token, newPassword, confirmPassword);
            
            User user = userRepository.findByResetToken(token)
                    .orElseThrow(() -> new Exception("resetPassword.invalidToken"));
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            
            logger.info("Password reset successfully for user: {}", user.getUsername());
            
        } catch (Exception e) {
            logger.error("Error during password reset for token: {}", token, e);
            throw e;
        }
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        try {
            if (userId == null) {
                throw new Exception("user.invalidId");
            }
            
            return userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("user.notFoundById," + userId));
                    
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", userId, e);
            throw e;
        }
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        try {
            if (!ValidationUtil.isNotBlank(username)) {
                throw new Exception("user.invalidUsername");
            }
            
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new Exception("user.notFoundByUsername," + username));
                    
        } catch (Exception e) {
            logger.error("Error getting user by username: {}", username, e);
            throw e;
        }
    }

    /**
     * Validate login input
     */
    private void validateLoginInput(LoginDto loginDto) throws Exception {
        if (loginDto == null) {
            throw new Exception("auth.invalidInput");
        }
        
        if (!ValidationUtil.isNotBlank(loginDto.getUsername())) {
            throw new Exception("auth.usernameRequired");
        }
        
        if (!ValidationUtil.isNotBlank(loginDto.getPassword())) {
            throw new Exception("auth.passwordRequired");
        }
    }

    /**
     * Validate registration input
     */
    private void validateRegistrationInput(RegisterDto registerDto) throws Exception {
        if (registerDto == null) {
            throw new Exception("register.invalidInput");
        }
        
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new Exception("register.passwordsNotMatch");
        }
        
        if (!ValidationUtil.isValidPassword(registerDto.getPassword(), 8)) {
            throw new Exception("register.passwordRequirements");
        }
    }

    /**
     * Validate password reset input
     */
    private void validatePasswordResetInput(String token, String newPassword, String confirmPassword) throws Exception {
        if (!ValidationUtil.isNotBlank(token)) {
            throw new Exception("resetPassword.tokenRequired");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("resetPassword.passwordsNotMatch");
        }
        
        if (!ValidationUtil.isValidPassword(newPassword, 8)) {
            throw new Exception("resetPassword.passwordRequirements");
        }
    }

    /**
     * Set authentication cookie
     */
    private void setAuthCookie(HttpServletResponse response, String token) {
        int maxAge = (int) (jwtUtil.getExpirationInSeconds());
        var cookie = CookieUtil.createPersistentCookie("tokenAuth", token, maxAge);
        CookieUtil.addCookie(response, cookie);
    }
}

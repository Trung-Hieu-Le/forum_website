package com.example.forum_website.service.impl;

import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.model.User;
import com.example.forum_website.enums.UserRole;
import com.example.forum_website.repository.UserRepository;
import com.example.forum_website.security.JwtUtil;
import com.example.forum_website.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            Cookie cookie = new Cookie("tokenAuth", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge((int) (jwtUtil.getExpiration() / 1000));
            response.addCookie(cookie);
            Cookie usernameCookie = new Cookie("usernameAuth", user.getUsername());
            usernameCookie.setPath("/");
            usernameCookie.setHttpOnly(true);
            usernameCookie.setMaxAge((int) (jwtUtil.getExpiration() / 1000));
            response.addCookie(usernameCookie);

        } catch (BadCredentialsException e) {
            throw new Exception("Invalid username or password");
        } catch (LockedException e) {
            throw new Exception("Account is locked");
        } catch (AuthenticationException e) {
            throw new Exception("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public void registerUser(RegisterDto registerDto) throws Exception {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new Exception("Username already exists");
        }
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new Exception("Email already exists");
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
                .orElseThrow(() -> new Exception("User with email " + email + " not found"));
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        // In a real app, send resetToken via email
        return resetToken;
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) throws Exception {
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Passwords do not match");
        }
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new Exception("Invalid reset token"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found with username: " + username));
    }
}

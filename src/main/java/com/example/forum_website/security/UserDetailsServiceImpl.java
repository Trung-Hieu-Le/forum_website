package com.example.forum_website.security;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.forum_website.model.User;
import com.example.forum_website.repository.UserRepository;

/**
 * Custom UserDetailsService implementation for Spring Security
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Attempted to load user with null or empty username");
                throw new UsernameNotFoundException("Username cannot be null or empty");
            }
            
            logger.debug("Loading user details for username: {}", username);
            
            Optional<User> userOpt = userRepository.findByUsername(username.trim());
            if (userOpt.isEmpty()) {
                logger.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            User user = userOpt.get();
            logger.debug("User found: {} with role: {}", username, user.getRole());
            
            return createUserDetails(user);
            
        } catch (UsernameNotFoundException e) {
            // Re-throw UsernameNotFoundException
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user details for username: {}", username, e);
            throw new UsernameNotFoundException("Error loading user details for: " + username, e);
        }
    }

    /**
     * Create CustomUserDetails from User entity
     * 
     * @param user User entity
     * @return CustomUserDetails object
     */
    private CustomUserDetails createUserDetails(User user) {
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                user.getId(),
                user.getRole().name()
        );
    }

    /**
     * Load user by ID (useful for JWT token processing)
     * 
     * @param userId user ID
     * @return UserDetails if found
     * @throws UsernameNotFoundException if user not found
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        try {
            if (userId == null) {
                logger.warn("Attempted to load user with null ID");
                throw new UsernameNotFoundException("User ID cannot be null");
            }
            
            logger.debug("Loading user details for ID: {}", userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                throw new UsernameNotFoundException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            logger.debug("User found by ID: {} with username: {}", userId, user.getUsername());
            
            return createUserDetails(user);
            
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user details for ID: {}", userId, e);
            throw new UsernameNotFoundException("Error loading user details for ID: " + userId, e);
        }
    }
}
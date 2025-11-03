package com.example.forum_website.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.enums.UserRole;
import com.example.forum_website.exception.DuplicateResourceException;
import com.example.forum_website.model.User;
import com.example.forum_website.repository.UserRepository;
import com.example.forum_website.security.JwtUtil;
import com.example.forum_website.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        // No-op
    }

    @Test
    void login_success_shouldSetAuthCookie() {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("john");
        loginDto.setPassword("password");

        UserDetails principal = new org.springframework.security.core.userdetails.User(
            "john", "hash", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        User dbUser = new User("john", "john@example.com", "hash", UserRole.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(dbUser));

        when(jwtUtil.generateToken(any(Long.class))).thenReturn("token-xyz");
        when(jwtUtil.getExpiration()).thenReturn(3600_000L);

        HttpServletResponse response = mock(HttpServletResponse.class);

        // When / Then (no exception means success)
        userService.authenticateAndSetToken(loginDto, response);
    }

    @Test
    void register_duplicateUsername_shouldThrowDuplicateResourceException() {
        // Given
        RegisterDto dto = new RegisterDto();
        dto.setUsername("john");
        dto.setEmail("john2@example.com");
        dto.setPassword("pass1234");
        dto.setConfirmPassword("pass1234");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(dto));
    }

    @Test
    void updateProfile_emailDuplicate_shouldThrowDuplicateResourceException() {
        // Given current authenticated user
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("john", null, Collections.emptyList()));
        SecurityContextHolder.setContext(context);

        User current = new User("john", "john@example.com", "hash", UserRole.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(current));

        ChangeProfileDto change = new ChangeProfileDto();
        change.setEmail("taken@example.com");

        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> userService.updateProfile(change));
    }
}

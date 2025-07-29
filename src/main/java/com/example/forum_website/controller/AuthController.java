package com.example.forum_website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletResponse response) {
        try {
            clearAuthCookies(response);
            model.addAttribute("loginDto", new LoginDto());
            return "client/login";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, HttpServletResponse response, Model model) {
        try {
            userService.authenticateAndSetToken(loginDto, response);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("loginDto", loginDto);
            model.addAttribute("error", e.getMessage());
            return "client/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpServletResponse response) {
        try {
            clearAuthCookies(response);
            if (!model.containsAttribute("registerDto")) {
                model.addAttribute("registerDto", new RegisterDto());
            }
            return "client/register";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("registerDto", registerDto);
                return "client/register";
            }
            userService.registerUser(registerDto);
            return "redirect:/login";
        } catch (Exception e) {
            result.reject("error", e.getMessage());
            model.addAttribute("registerDto", registerDto);
            return "client/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model, HttpServletResponse response) {
        try {
            clearAuthCookies(response);
            model.addAttribute("email", "");
            return "client/forgot-password";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/forgot-password")
    public String initiatePasswordReset(@RequestParam String email, HttpSession session) {
        try {
            String resetToken = userService.initiatePasswordReset(email);
            session.setAttribute("resetPasswordToken", resetToken);
            return "redirect:/reset-password";
        } catch (Exception e) {
            return "redirect:/forgot-password?error=" + e.getMessage();
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session, Model model) {
        try {
            String token = (String) session.getAttribute("resetPasswordToken");
            if (token == null) {
                return "redirect:/forgot-password?error=Invalid or expired token";
            }
            model.addAttribute("token", token);
            return "client/reset-password";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword,
            @RequestParam String confirmPassword, HttpSession session) {
        try {
            userService.resetPassword(token, newPassword, confirmPassword);
            session.removeAttribute("resetPasswordToken");
            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            return "redirect:/reset-password?error=" + e.getMessage();
        }
    }

    private void clearAuthCookies(HttpServletResponse response) {
    Cookie tokenCookie = new Cookie("tokenAuth", null);
    tokenCookie.setPath("/");
    tokenCookie.setMaxAge(0);
    tokenCookie.setHttpOnly(true);
    response.addCookie(tokenCookie);

    Cookie usernameCookie = new Cookie("usernameAuth", null);
    usernameCookie.setPath("/");
    usernameCookie.setMaxAge(0);
    usernameCookie.setHttpOnly(true);
    response.addCookie(usernameCookie);
}
}

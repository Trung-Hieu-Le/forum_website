package com.example.forum_website.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.forum_website.dto.ApiResponse;
import com.example.forum_website.dto.LoginDto;
import com.example.forum_website.dto.RegisterDto;
import com.example.forum_website.service.UserService;
import com.example.forum_website.util.MessageUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

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
    @ResponseBody
    public ApiResponse login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            userService.authenticateAndSetToken(loginDto, response);
            String message = messageUtil.getMessage("login.success", null);
            return new ApiResponse("ok", "success", List.of(message), "/");
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", "danger", List.of(errorMessage), null);
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
    @ResponseBody
    public ApiResponse register(@Valid @RequestBody RegisterDto registerDto, BindingResult result,
            HttpServletResponse response) {
        if (result.hasErrors()) {
            List<String> errs = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return new ApiResponse("error", "warning", errs, null);
        }
        try {
            userService.registerUser(registerDto);
            String message = messageUtil.getMessage("register.success", null);
            return new ApiResponse("ok", "success", List.of(message), "/login");
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", "danger", List.of(errorMessage), null);
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
    @ResponseBody
    public ApiResponse initiatePasswordReset(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            String errorMessage = messageUtil.getMessage("forgotPassword.email.notBlank", null);
            return new ApiResponse("error", "warning", List.of(errorMessage), null);
        }
        try {
            String token = userService.initiatePasswordReset(email);
            session.setAttribute("resetPasswordToken", token);
            String message = messageUtil.getMessage("forgotPassword.success", null);
            return new ApiResponse("ok", "success", List.of(message), "/reset-password");
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", "danger", List.of(errorMessage), null);
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session, Model model) {
        try {
            String token = (String) session.getAttribute("resetPasswordToken");
            if (token == null) {
                return "redirect:/forgot-password?error=resetPassword.invalidToken";
            }
            model.addAttribute("token", token);
            return "client/reset-password";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public ApiResponse resetPassword(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            String token = body.get("token");
            String newPassword = body.get("newPassword");
            String confirmPassword = body.get("confirmPassword");
            userService.resetPassword(token, newPassword, confirmPassword);
            session.removeAttribute("resetPasswordToken");
            String message = messageUtil.getMessage("resetPassword.success", null);
            return new ApiResponse("ok", "success", List.of(message), "/login");
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", "danger", List.of(errorMessage), null);
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

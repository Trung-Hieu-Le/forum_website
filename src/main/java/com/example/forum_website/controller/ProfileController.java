package com.example.forum_website.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.forum_website.dto.ApiResponse;
import com.example.forum_website.dto.ChangePasswordDto;
import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.enums.ToastType;
import com.example.forum_website.service.UserService;
import com.example.forum_website.util.MessageUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageUtil messageUtil;
    
    @PutMapping("/update")
    public ApiResponse updateProfile(@Valid @RequestBody ChangeProfileDto changeProfileDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, (Map<String, Object>) (Map<?, ?>) fieldErrors);
        }
        try {
            // TODO: Implement updateProfile logic
            String message = messageUtil.getMessage("profile.update.success", null);
            return new ApiResponse("ok", ToastType.SUCCESS, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }
    
    @PostMapping("/change-password")
    public ApiResponse changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, (Map<String, Object>) (Map<?, ?>) fieldErrors);
        }
        try {
            // TODO: Implement changePassword logic
            String message = messageUtil.getMessage("profile.changePassword.success", null);
            return new ApiResponse("ok", ToastType.SUCCESS, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }
}

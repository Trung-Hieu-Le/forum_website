package com.example.forum_website.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.example.forum_website.dto.ApiResponse;
import com.example.forum_website.dto.ChangePasswordDto;
import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.enums.ToastType;
import com.example.forum_website.service.UserService;
import com.example.forum_website.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class SettingController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @Value("${app.upload.dir:src/main/resources/static/avatar}")
    private String uploadDir;

    private static final List<String> SUPPORTED_LANGUAGES = List.of("vi", "en", "ja");
    private static final List<String> SUPPORTED_THEMES = List.of("light", "dark", "darkblue");
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @GetMapping("/change-language")
    public String changeLanguage(HttpServletRequest request, HttpServletResponse response, @RequestParam String lang) {
        if (SUPPORTED_LANGUAGES.contains(lang)) {
            Locale locale = switch (lang) {
                case "en" -> Locale.ENGLISH;
                case "ja" -> Locale.JAPANESE;
                default -> new Locale("vi");
            };
            LocaleResolver resolver = RequestContextUtils.getLocaleResolver(request);
            if (resolver != null) {
                resolver.setLocale(request, response, locale);
            }
        }
        return "redirect:" + Optional.ofNullable(request.getHeader("Referer")).orElse("/");
    }

    @GetMapping("/change-theme")
    public String changeTheme(HttpServletRequest request, @RequestParam String theme) {
        if (SUPPORTED_THEMES.contains(theme)) {
            request.getSession().setAttribute("theme", theme);
        }
        return "redirect:" + Optional.ofNullable(request.getHeader("Referer")).orElse("/");
    }

    // Settings page with tab support
    @GetMapping("/settings")
    public String settingsPage(@RequestParam(defaultValue = "profile") String tab, Model model) {
        model.addAttribute("activeTab", tab);
        model.addAttribute("changeProfileDto", new ChangeProfileDto());
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        
        // Add notification settings for notifications tab
        if ("notifications".equals(tab)) {
            try {
                var user = userService.getCurrentUser();
                model.addAttribute("notificationSettings", Map.of(
                    "emailNewPost", user.isEmailNewPost(),
                    "emailReply", user.isEmailReply(),
                    "emailMention", user.isEmailMention(),
                    "browserNotifications", user.isBrowserNotifications()
                ));
            } catch (Exception e) {
                // Default values if user not found
                model.addAttribute("notificationSettings", Map.of(
                    "emailNewPost", true,
                    "emailReply", true,
                    "emailMention", true,
                    "browserNotifications", false
                ));
            }
        }
        
        return "client/settings/settings";
    }

    // Update profile endpoint
    @PostMapping("/settings/profile")
    @ResponseBody
    public ApiResponse updateProfile(@Valid @RequestBody ChangeProfileDto changeProfileDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> (Object) messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, fieldErrors);
        }
        try {
            userService.updateProfile(changeProfileDto);
            String message = messageUtil.getMessage("profile.update.success", null);
            return new ApiResponse("ok", ToastType.SUCCESS, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    // Change password endpoint
    @PostMapping("/settings/password")
    @ResponseBody
    public ApiResponse changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> (Object) messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, fieldErrors);
        }
        try {
            userService.changePassword(changePasswordDto);
            String message = messageUtil.getMessage("profile.changePassword.success", null);
            return new ApiResponse("ok", ToastType.SUCCESS, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    // Update notification settings endpoint
    @PostMapping("/settings/notifications")
    @ResponseBody
    public ApiResponse updateNotifications(@RequestBody Map<String, Object> notificationSettings) {
        try {
            userService.updateNotificationSettings(notificationSettings);
            String message = messageUtil.getMessage("settings.notifications.updated", null);
            return new ApiResponse("ok", ToastType.SUCCESS, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    // Upload avatar endpoint
    @PostMapping(value = "/settings/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                String message = messageUtil.getMessage("avatar.upload.empty", null);
                return new ApiResponse("error", ToastType.ERROR, message);
            }

            if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                String message = messageUtil.getMessage("avatar.upload.invalidType", null);
                return new ApiResponse("error", ToastType.ERROR, message);
            }

            if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
                String message = messageUtil.getMessage("avatar.upload.tooLarge", null);
                return new ApiResponse("error", ToastType.ERROR, message);
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Get current user to check old avatar
            String oldAvatar = userService.getCurrentUserAvatar();
            
            // Update user avatar in database
            userService.updateAvatar(filename);
            
            // Delete old avatar file if it exists
            if (oldAvatar != null && !oldAvatar.isEmpty()) {
                try {
                    Path oldAvatarPath = Paths.get(uploadDir, oldAvatar);
                    if (Files.exists(oldAvatarPath)) {
                        Files.delete(oldAvatarPath);
                    }
                } catch (IOException e) {
                    // Log error but don't fail the upload
                    System.err.println("Failed to delete old avatar: " + e.getMessage());
                }
            }

            String message = messageUtil.getMessage("avatar.upload.success", null);
            Map<String, Object> data = Map.of("filename", filename);
            return new ApiResponse("ok", ToastType.SUCCESS, message, data);
        } catch (IOException e) {
            String message = messageUtil.getMessage("avatar.upload.error", null);
            return new ApiResponse("error", ToastType.ERROR, message);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }
}


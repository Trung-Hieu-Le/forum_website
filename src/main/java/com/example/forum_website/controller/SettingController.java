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

import com.example.forum_website.constant.AppConstants;
import com.example.forum_website.dto.ApiResponse;
import com.example.forum_website.dto.ChangePasswordDto;
import com.example.forum_website.dto.ChangeProfileDto;
import com.example.forum_website.enums.ToastType;
import com.example.forum_website.service.UserService;
import com.example.forum_website.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SettingController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @Value("${app.upload.dir:" + AppConstants.DEFAULT_AVATAR_UPLOAD_DIR + "}")
    private String uploadDir;

    private static final List<String> SUPPORTED_LANGUAGES = List.of(AppConstants.SUPPORTED_LANGUAGES);
    private static final List<String> SUPPORTED_THEMES = List.of(AppConstants.SUPPORTED_THEMES);
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(AppConstants.ALLOWED_IMAGE_TYPES);

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

    // Get current user data as JSON
    @GetMapping("/api/settings/current-user")
    @ResponseBody
    public ApiResponse getCurrentUserData() {
        try {
            var user = userService.getCurrentUser();
            Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "fullname", user.getFullname() != null ? user.getFullname() : "",
                "avatar", user.getAvatar() != null ? user.getAvatar() : "default-avatar.png"
            );
            return new ApiResponse("ok", ToastType.SUCCESS, null, userData);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    // Settings page with tab support
    @GetMapping("/settings")
    public String settingsPage(@RequestParam(defaultValue = "profile") String tab, Model model) {
        model.addAttribute("activeTab", tab);
        model.addAttribute("changeProfileDto", new ChangeProfileDto());
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "client/settings/settings";
    }

    // Update profile endpoint
    @PostMapping("/api/settings/profile")
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
    @PostMapping("/api/settings/password")
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

    // Get notification settings endpoint
    @GetMapping("/api/settings/notifications")
    @ResponseBody
    public ApiResponse getNotificationSettings() {
        try {
            var user = userService.getCurrentUser();
            Map<String, Object> notificationSettings = Map.of(
                "emailNewPost", user.isEmailNewPost(),
                "emailReply", user.isEmailReply(),
                "emailMention", user.isEmailMention(),
                "browserNotifications", user.isBrowserNotifications()
            );
            return new ApiResponse("ok", ToastType.SUCCESS, null, notificationSettings);
        } catch (Exception e) {
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    // Update notification settings endpoint
    @PostMapping("/api/settings/notifications")
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
    @PostMapping(value = "/api/settings/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        log.info("Avatar upload request: size={} bytes, contentType={}", file.getSize(), file.getContentType());
        try {
            if (file.isEmpty()) {
                String message = messageUtil.getMessage("avatar.upload.empty", null);
                return new ApiResponse("error", ToastType.ERROR, message);
            }

            if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                String message = messageUtil.getMessage("avatar.upload.invalidType", null);
                return new ApiResponse("error", ToastType.ERROR, message);
            }

            if (file.getSize() > AppConstants.MAX_AVATAR_FILE_SIZE) {
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
                    log.warn("Failed to delete old avatar {}: {}", oldAvatar, e.getMessage());
                }
            }

            log.info("Avatar uploaded successfully: {}", filename);
            String message = messageUtil.getMessage("avatar.upload.success", null);
            Map<String, Object> data = Map.of("filename", filename);
            return new ApiResponse("ok", ToastType.SUCCESS, message, data);
        } catch (IOException e) {
            log.error("IO error during avatar upload: {}", e.getMessage());
            String message = messageUtil.getMessage("avatar.upload.error", null);
            return new ApiResponse("error", ToastType.ERROR, message);
        } catch (Exception e) {
            log.error("Avatar upload failed: {}", e.getMessage());
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }
}


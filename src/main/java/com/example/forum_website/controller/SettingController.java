package com.example.forum_website.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SettingController {

    private static final List<String> SUPPORTED_LANGUAGES = List.of("vi", "en", "ja");
    private static final List<String> SUPPORTED_THEMES = List.of("light", "dark", "darkblue");

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
}


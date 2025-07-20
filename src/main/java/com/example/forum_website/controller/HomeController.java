package com.example.forum_website.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @GetMapping({"/", "/home1"})
    public String home1() {
        return "client/home1";
    }

    @GetMapping("/home2")
    public String home2(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<String> roles = auth.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .collect(Collectors.toList());
        model.addAttribute("username", username);
        model.addAttribute("roles", roles);
        return "client/home2";
    }

    @GetMapping("/home3")
    @PreAuthorize("hasRole('ADMIN')")
    public String home3() {
        return "client/home3";
    }
}

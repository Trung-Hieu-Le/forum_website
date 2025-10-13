package com.example.forum_website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.forum_website.constant.AppConstants;
import com.example.forum_website.service.ThreadService;
import com.example.forum_website.model.Thread;

@Controller
public class HomeController {
    @Autowired
    private ThreadService threadService;

    @Value("${thread.page.size:" + AppConstants.DEFAULT_THREAD_PAGE_SIZE + "}")
    private int pageSize;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Page<Thread> threads = threadService.getThreads(0, pageSize);
        model.addAttribute("threads", threads.getContent());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", threads.getTotalPages());
        return "client/home";
    }

    @GetMapping("/home2")
    public String home2(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("");
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "client/home2";
    }

    @GetMapping("/home3")
    public String home3() {
        return "client/home3";
    }

    @GetMapping("/api/threads")
    @ResponseBody
    public Page<Thread> getMoreThreads(@RequestParam("page") int page) {
        return threadService.getThreads(page, pageSize);
    }
}

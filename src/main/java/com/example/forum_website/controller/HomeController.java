package com.example.forum_website.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.forum_website.constant.AppConstants;
import com.example.forum_website.dto.ApiResponse;
import com.example.forum_website.dto.CreateThreadDto;
import com.example.forum_website.dto.UpdateThreadDto;
import com.example.forum_website.enums.ToastType;
import com.example.forum_website.model.Thread;
import com.example.forum_website.model.Topic;
import com.example.forum_website.repository.TopicRepository;
import com.example.forum_website.service.ThreadService;
import com.example.forum_website.util.MessageUtil;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
    @Autowired
    private ThreadService threadService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageUtil messageUtil;

    @Value("${thread.page.size:" + AppConstants.DEFAULT_THREAD_PAGE_SIZE + "}")
    private int pageSize;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Page<Thread> threads = threadService.getPublicThreads(0, pageSize);
        List<Topic> topics = topicRepository.findAllByOrderByNameAsc();
        model.addAttribute("threads", threads.getContent());
        model.addAttribute("topics", topics);
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", threads.getTotalPages());
        return "client/home";
    }

    @GetMapping("/api/threads")
    @ResponseBody
    public Page<Thread> getMoreThreads(@RequestParam("page") int page) {
        return threadService.getPublicThreads(page, pageSize);
    }

    @GetMapping("/api/topics")
    @ResponseBody
    public ApiResponse getTopics() {
        try {
            List<Topic> topics = topicRepository.findAllByOrderByNameAsc();
            List<Map<String, Object>> topicData = topics.stream()
                    .map(topic -> {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", topic.getId());
                        map.put("name", topic.getName());
                        return map;
                    })
                    .collect(Collectors.toList());
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("topics", topicData);
            return new ApiResponse("ok", ToastType.SUCCESS, null, data);
        } catch (Exception e) {
            log.error("Error fetching topics: {}", e.getMessage());
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    @PostMapping("/api/threads")
    @ResponseBody
    public ApiResponse createThread(@Valid @RequestBody CreateThreadDto createThreadDto, BindingResult result) {
        log.info("Creating new thread: title={}", createThreadDto.getTitle());
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            Map<String, Object> errorData = new java.util.HashMap<>();
            errorData.putAll(fieldErrors);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, errorData);
        }
        try {
            Thread thread = threadService.createThread(createThreadDto);
            String message = messageUtil.getMessage("home.post.success", null);
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("threadId", thread.getId());
            return new ApiResponse("ok", ToastType.SUCCESS, message, data);
        } catch (Exception e) {
            log.warn("Failed to create thread: {}", e.getMessage());
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    @GetMapping("/api/threads/{id}")
    @ResponseBody
    public ApiResponse getThread(@PathVariable Long id) {
        try {
            Thread thread = threadService.getThreadById(id);
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("thread", thread);
            return new ApiResponse("ok", ToastType.SUCCESS, null, data);
        } catch (Exception e) {
            log.error("Error fetching thread: {}", e.getMessage());
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }

    @PutMapping("/api/threads/{id}")
    @ResponseBody
    public ApiResponse updateThread(@PathVariable Long id, @Valid @RequestBody UpdateThreadDto updateThreadDto, BindingResult result) {
        log.info("Updating thread: id={}, title={}", id, updateThreadDto.getTitle());
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> messageUtil.getMessage(error.getDefaultMessage(), null),
                            (existing, replacement) -> existing + "; " + replacement
                    ));
            String validationMessage = messageUtil.getMessage("validation.failed", null);
            Map<String, Object> errorData = new java.util.HashMap<>();
            errorData.putAll(fieldErrors);
            return new ApiResponse("error", ToastType.ERROR, validationMessage, errorData);
        }
        try {
            Thread thread = threadService.updateThread(id, updateThreadDto);
            String message = messageUtil.getMessage("home.post.update.success", null);
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("threadId", thread.getId());
            return new ApiResponse("ok", ToastType.SUCCESS, message, data);
        } catch (Exception e) {
            log.warn("Failed to update thread: {}", e.getMessage());
            String errorMessage = messageUtil.resolveErrorMessage(e);
            return new ApiResponse("error", ToastType.ERROR, errorMessage);
        }
    }
}

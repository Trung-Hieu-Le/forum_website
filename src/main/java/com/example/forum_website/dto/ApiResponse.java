package com.example.forum_website.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private String status;
    private String errorTag; // info, success, warning, danger
    private String message;
    private List<String> messages;
    private String redirectUrl;

    public ApiResponse(String status, String errorTag, String message, String redirectUrl) {
        this.status = status;
        this.errorTag = errorTag;
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

    public ApiResponse(String status, String errorTag, List<String> messages, String redirectUrl) {
        this.status = status;
        this.errorTag = errorTag;
        this.messages = messages;
        this.redirectUrl = redirectUrl;
    }
}

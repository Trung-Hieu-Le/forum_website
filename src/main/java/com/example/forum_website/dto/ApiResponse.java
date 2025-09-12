package com.example.forum_website.dto;

import java.util.Map;

import com.example.forum_website.enums.ToastType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private String status;
    private ToastType type = ToastType.UNKNOWN;
    private String message;
    private Map<String, Object> data;

    public ApiResponse(String status, ToastType type, String message, Map<String, Object> data) {
        this.status = status;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String status, ToastType type, String message) {
        this.status = status;
        this.type = type;
        this.message = message;
        this.data = null;
    }
}
package com.example.forum_website.utils;

import java.util.List;

import com.example.forum_website.dto.ApiResponse;

/**
 * Utility class for building API responses
 */
public class ResponseBuilder {

    /**
     * Build success response with toast
     * 
     * @param messageKey message key for internationalization
     * @param redirectUrl URL to redirect after success
     * @return ApiResponse
     */
    public static ApiResponse success(String messageKey, String redirectUrl) {
        return new ApiResponse("ok", "success", messageKey, redirectUrl, "toast");
    }

    /**
     * Build success response with custom message and toast
     * 
     * @param message custom message
     * @param redirectUrl URL to redirect after success
     * @return ApiResponse
     */
    public static ApiResponse successWithMessage(String message, String redirectUrl) {
        return new ApiResponse("ok", "success", message, redirectUrl, "toast");
    }

    /**
     * Build success response without redirect
     * 
     * @param messageKey message key for internationalization
     * @return ApiResponse
     */
    public static ApiResponse success(String messageKey) {
        return new ApiResponse("ok", "success", messageKey, null, "toast");
    }

    /**
     * Build error response with toast
     * 
     * @param messageKey message key for internationalization
     * @return ApiResponse
     */
    public static ApiResponse error(String messageKey) {
        return new ApiResponse("error", "danger", messageKey, null, "toast");
    }

    /**
     * Build error response with custom message and toast
     * 
     * @param message custom error message
     * @return ApiResponse
     */
    public static ApiResponse errorWithMessage(String message) {
        return new ApiResponse("error", "danger", message, null, "toast");
    }

    /**
     * Build warning response
     * 
     * @param messageKey message key for internationalization
     * @return ApiResponse
     */
    public static ApiResponse warning(String messageKey) {
        return new ApiResponse("error", "warning", messageKey, null);
    }

    /**
     * Build warning response with custom message
     * 
     * @param message custom warning message
     * @return ApiResponse
     */
    public static ApiResponse warningWithMessage(String message) {
        return new ApiResponse("error", "warning", message, null);
    }

    /**
     * Build validation error response
     * 
     * @param errors list of validation errors
     * @return ApiResponse
     */
    public static ApiResponse validationError(List<String> errors) {
        return new ApiResponse("error", "warning", errors, null);
    }

    /**
     * Build validation error response with single error
     * 
     * @param error single validation error
     * @return ApiResponse
     */
    public static ApiResponse validationError(String error) {
        return new ApiResponse("error", "warning", error, null);
    }

    /**
     * Build info response
     * 
     * @param messageKey message key for internationalization
     * @return ApiResponse
     */
    public static ApiResponse info(String messageKey) {
        return new ApiResponse("ok", "primary", messageKey, null, "toast");
    }

    /**
     * Build info response with custom message
     * 
     * @param message custom info message
     * @return ApiResponse
     */
    public static ApiResponse infoWithMessage(String message) {
        return new ApiResponse("ok", "primary", message, null, "toast");
    }

    /**
     * Build custom response
     * 
     * @param status response status
     * @param errorTag error tag (success, warning, danger, primary)
     * @param message response message
     * @param redirectUrl redirect URL
     * @param type response type (toast, modal, etc.)
     * @return ApiResponse
     */
    public static ApiResponse custom(String status, String errorTag, String message, String redirectUrl, String type) {
        return new ApiResponse(status, errorTag, message, redirectUrl, type);
    }

    /**
     * Build custom response without type
     * 
     * @param status response status
     * @param errorTag error tag
     * @param message response message
     * @param redirectUrl redirect URL
     * @return ApiResponse
     */
    public static ApiResponse custom(String status, String errorTag, String message, String redirectUrl) {
        return new ApiResponse(status, errorTag, message, redirectUrl);
    }
} 
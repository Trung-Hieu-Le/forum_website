package com.example.forum_website.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling internationalized messages
 */
@Component
public class MessageUtil {

    @Autowired
    private MessageSource messageSource;

    /**
     * Get message by code with default locale
     * 
     * @param code message code
     * @return localized message
     */
    public String getMessage(String code) {
        return getMessage(code, (Object[]) null);
    }

    /**
     * Get message by code with arguments
     * 
     * @param code message code
     * @param args arguments for message formatting
     * @return localized message
     */
    public String getMessage(String code, Object[] args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return "Message not found for code: " + code;
        }
    }

    /**
     * Get message by code with default message fallback
     * 
     * @param code message code
     * @param defaultMessage default message if code not found
     * @return localized message or default message
     */
    public String getMessage(String code, String defaultMessage) {
        try {
            return messageSource.getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }

    /**
     * Get message by code with arguments and default message fallback
     * 
     * @param code message code
     * @param args arguments for message formatting
     * @param defaultMessage default message if code not found
     * @return localized message or default message
     */
    public String getMessage(String code, Object[] args, String defaultMessage) {
        try {
            return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }

    /**
     * Resolve error message from exception
     * Expected format: "messageCode,arg1,arg2,..."
     * 
     * @param e exception containing message
     * @return resolved localized message
     */
    public String resolveErrorMessage(Exception e) {
        if (e.getMessage() == null) {
            return getMessage("error.unknown", "An unknown error occurred");
        }

        String[] parts = e.getMessage().split(",");
        String messageKey = parts[0];
        Object[] args = parts.length > 1 ? new Object[parts.length - 1] : null;
        
        if (args != null) {
            for (int i = 1; i < parts.length; i++) {
                args[i - 1] = parts[i];
            }
        }

        return getMessage(messageKey, args, e.getMessage());
    }

    /**
     * Get current locale
     * 
     * @return current locale string
     */
    public String getCurrentLocale() {
        return LocaleContextHolder.getLocale().toString();
    }

    /**
     * Check if message exists for given code
     * 
     * @param code message code
     * @return true if message exists
     */
    public boolean hasMessage(String code) {
        try {
            messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }
} 
package com.example.forum_website.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageUtil {
    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object[] args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return "Message not found for code: " + code;
        }
    }

    public String resolveErrorMessage(Exception e) {
        String[] parts = e.getMessage().split(",");
        String messageKey = parts[0];
        Object[] args = parts.length > 1 ? new Object[]{parts[1]} : null;
        return getMessage(messageKey, args);
    }
}

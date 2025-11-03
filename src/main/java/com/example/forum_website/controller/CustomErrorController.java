package com.example.forum_website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.forum_website.util.MessageUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    private MessageUtil messageUtil;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 500;
        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        log.warn("Error {} occurred at {}", statusCode, requestUri);

        String messageKey = switch (statusCode) {
            case 400 -> "error.badRequest";
            case 401 -> "error.unauthorized";
            case 403 -> "error.forbidden";
            case 404 -> "error.notFound";
            case 405 -> "error.methodNotAllowed";
            case 408 -> "error.requestTimeout";
            case 409 -> "error.conflict";
            case 415 -> "error.unsupportedMediaType";
            case 500 -> "error.internalServerError";
            case 502 -> "error.badGateway";
            case 503 -> "error.serviceUnavailable";
            case 504 -> "error.gatewayTimeout";
            default -> "error.unknown";
        };

        String errorTitle = messageUtil.getMessage("error.title", new Object[]{statusCode});
        String errorMessage = messageUtil.getMessage(messageKey, null);

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);

        return "error/error";
    }
}

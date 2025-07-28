package com.example.forum_website.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

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

        String message = switch (statusCode) {
            case 400 -> "Yêu cầu không hợp lệ.";
            case 401 -> "Bạn cần đăng nhập để truy cập tài nguyên này.";
            case 403 -> "Bạn không có quyền truy cập trang này.";
            case 404 -> "Trang bạn tìm kiếm không tồn tại.";
            case 405 -> "Phương thức yêu cầu không được hỗ trợ.";
            case 408 -> "Yêu cầu đã hết thời gian chờ.";
            case 409 -> "Yêu cầu bị xung đột với trạng thái hiện tại.";
            case 415 -> "Loại dữ liệu không được hỗ trợ.";
            case 500 -> "Lỗi máy chủ. Vui lòng thử lại sau.";
            case 502 -> "Máy chủ trung gian nhận được phản hồi không hợp lệ.";
            case 503 -> "Dịch vụ hiện không khả dụng. Vui lòng thử lại sau.";
            case 504 -> "Hết thời gian phản hồi từ máy chủ.";
            default -> "Đã xảy ra lỗi không xác định.";
        };

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", "Lỗi " + statusCode);
        model.addAttribute("errorMessage", message);

        return "error/error"; // Sử dụng error/error.html
    }

}

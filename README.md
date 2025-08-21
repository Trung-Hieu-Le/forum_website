# Forum Website - Tối ưu hóa Code

## Tổng quan

Dự án này đã được tối ưu hóa để dễ đọc, dễ bảo trì và tránh lặp code. Các chức năng chính đã được tách riêng thành các module JavaScript riêng biệt.

## Cấu trúc JavaScript mới

### 1. `main.js` - Module chính
- Khởi tạo và quản lý tất cả các module
- Xử lý lỗi toàn cục
- Cung cấp các utility method

### 2. `authentication.js` - Xử lý authentication
- Login, Register, Forgot Password, Reset Password
- Xử lý form submission
- Gọi API và xử lý response
- Tự động redirect khi thành công

### 3. `validation.js` - Client-side validation
- Validation real-time cho các form
- Hỗ trợ nhiều loại validation (required, email, min/max length, pattern)
- Custom validation support
- Hiển thị error message đẹp mắt

### 4. `toast.js` - Toast notifications
- Hiển thị toast messages
- Hỗ trợ nhiều loại status (success, warning, danger, info)
- Auto-hide với progress bar
- Responsive design

## Cách sử dụng

### 1. Form Validation
```html
<form data-validate="true">
    <input type="email" required data-field-name="Email">
    <input type="password" required minlength="8" data-field-name="Password">
</form>
```

### 2. Toast Notifications
```javascript
// Từ bất kỳ đâu trong code
window.app.showToast('success', 'Operation completed successfully!');

// Hoặc dispatch event
document.dispatchEvent(new CustomEvent('showToast', {
    detail: { status: 'success', message: 'Success!' }
}));
```

### 3. API Response với Toast
```java
// Trong Controller
return new ApiResponse("ok", "success", "Login successful", "/", "toast");
```

## Cải tiến đã thực hiện

### 1. Tách biệt concerns
- JavaScript được tách thành các module riêng biệt
- CSS được tổ chức theo chức năng
- HTML sạch sẽ, không còn data-ajax
- Java utility classes được tách riêng để tái sử dụng

### 2. Validation system
- Client-side validation real-time
- Error messages đẹp mắt
- Hỗ trợ nhiều loại validation rules

### 3. Toast system
- Chỉ hiển thị toast khi API response có type="toast"
- Hỗ trợ nhiều loại status
- Auto-hide với progress bar

### 4. Authentication flow
- Xử lý form submission tự động
- Error handling tốt hơn
- Redirect tự động khi thành công

## Cấu trúc file

```
src/main/resources/
├── static/
│   ├── js/
│   │   ├── main.js              # Module chính
│   │   ├── authentication.js    # Xử lý authentication
│   │   ├── validation.js        # Client-side validation
│   │   └── toast.js             # Toast notifications
│   └── css/
│       ├── authentication.css   # CSS cho auth pages
│       └── theme.css            # CSS chung
└── templates/
    └── client/
        ├── layouts/
        │   ├── base.html         # Layout chính
        │   └── head.html         # Head section
        ├── login.html            # Login page
        ├── register.html         # Register page
        ├── forgot-password.html  # Forgot password page
        └── reset-password.html   # Reset password page

src/main/java/com/example/forum_website/
├── utils/
│   ├── MessageUtil.java         # Xử lý internationalization
│   ├── ValidationUtil.java      # Validation utilities
│   └── CookieUtil.java          # Cookie operations
├── controller/
├── service/
└── dto/
```

## Lưu ý

1. **Bỏ data-ajax**: Tất cả form đã được chuyển sang sử dụng JavaScript thuần
2. **Toast system**: Chỉ hiển thị toast khi API response có type="toast"
3. **Validation**: Sử dụng data-validate="true" thay vì data-ajax="true"
4. **CSS**: Mỗi chức năng có CSS riêng để dễ bảo trì
5. **Utility classes**: MessageSource, validation, và cookie operations đã được tách thành utility classes riêng

## Hướng dẫn phát triển

### Thêm validation mới
1. Cập nhật `validation.js` với rule mới
2. Thêm CSS tương ứng
3. Cập nhật HTML với attributes cần thiết

### Thêm toast mới
1. Sử dụng `window.app.showToast(status, message)`
2. Hoặc dispatch custom event

### Thêm form mới
1. Sử dụng `data-validate="true"`
2. Thêm ID duy nhất cho form
3. Cập nhật `authentication.js` nếu cần

### Sử dụng utility classes
1. **MessageUtil**: Xử lý internationalization
   ```java
   @Autowired
   private MessageUtil messageUtil;
   
   String message = messageUtil.getMessage("key");
   ```

2. **ValidationUtil**: Validation logic
   ```java
   if (!ValidationUtil.isValidEmail(email)) {
       // handle invalid email
   }
   ```

3. **CookieUtil**: Cookie operations
   ```java
   CookieUtil.clearCookies(response, "cookie1", "cookie2");
   ```

## Dependencies

- Bootstrap 5.x
- jQuery 3.6.0
- Font Awesome 6.4.0
- Spring Boot (Backend) 
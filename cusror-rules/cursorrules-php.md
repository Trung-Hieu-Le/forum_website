# Hướng dẫn Coding cho Java Spring Boot

## I. Coding Checklist

### 1. Không có code thừa (Clean Code)
- (PASS/FAIL) Tránh code chết, code bị comment, và trùng lặp (DRY)
- (PASS/FAIL) Đặt tên có ý nghĩa cho class, method, biến
- (PASS/FAIL) Giữ method nhỏ gọn, tập trung (ưu tiên ≤ 30 dòng)

Ví dụ:
```java
// Không tốt
public void p(U u) {}

// Tốt
public void processUserRegistration(UserDto userDto) {}
```

### 2. Comment cho logic phức tạp
- (PASS/FAIL) Chỉ comment cho mục đích không hiển nhiên, bất biến, case đặc biệt
- (PASS/FAIL) Ưu tiên code tự diễn giải và tên tốt hơn là comment

Ví dụ:
```java
// Giải thích lý do bảo mật hoặc lưu ý hiệu năng nếu không hiển nhiên
// Token phải dùng một lần để ngăn tấn công replay
```

### 3. Xử lý lỗi (Error handling)
- (PASS/FAIL) Ném/bắt exception cụ thể; không được nuốt lỗi
- (PASS/FAIL) Trả HTTP status phù hợp trong controller
- (PASS/FAIL) Dùng `@ControllerAdvice` + `@ExceptionHandler` để map lỗi toàn cục

Ví dụ:
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) { super("User không tồn tại: " + id); }
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("USER_NOT_FOUND", ex.getMessage()));
    }
}
```

### 4. Quản lý transaction
- (PASS/FAIL) Dùng `@Transactional` ở service layer, không đặt ở controller
- (PASS/FAIL) Giữ transaction ngắn; tránh gọi network bên trong
- (PASS/FAIL) Chọn propagation và isolation phù hợp khi cần

Ví dụ:
```java
@Service
public class PaymentService {
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // load, validate, cập nhật số dư
    }
}
```

### 5. Hiệu năng (Performance)
- (PASS/FAIL) Tránh N+1 queries (dùng fetch join hoặc entity graph)
- (PASS/FAIL) Dùng phân trang cho các endpoint danh sách
- (PASS/FAIL) Cache cẩn trọng với `@Cacheable` khi phù hợp

Ví dụ fix N+1 JPA:
```java
@Query("select u from User u left join fetch u.roles where u.id = :id")
Optional<User> findWithRoles(@Param("id") Long id);
```

### 6. Logging
- (PASS/FAIL) Dùng SLF4J (`@Slf4j`) và mức log phù hợp (INFO/WARN/ERROR)
- (PASS/FAIL) Không log dữ liệu nhạy cảm (password, token)
- (PASS/FAIL) Thêm correlation ID cho request (ví dụ: MDC)

Ví dụ:
```java
@Slf4j
@Service
public class AuthService {
    public void login(String username) {
        log.info("Login attempt for user={}", username);
    }
}
```

### 7. Cấu hình & hằng số
- (PASS/FAIL) Dùng `application.properties`/`application.yml` và `@ConfigurationProperties`
- (PASS/FAIL) Không hardcode secrets; dùng biến môi trường/kho bí mật
- (PASS/FAIL) Đặt hằng số trong class riêng; tránh magic number

Ví dụ:
```java
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String issuer, String secret, Duration ttl) {}
```

### 8. Bảo mật (Spring Security)
- (PASS/FAIL) Dựa vào Spring Security; tránh tự viết crypto
- (PASS/FAIL) Hash mật khẩu bằng BCrypt; validate input; chống XSS/CSRF
- (PASS/FAIL) Xác thực JWT/session phía server; implement logout/expiration

Ví dụ:
```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/assets/**").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
}
```

### 9. Thiết kế RESTful API
- (PASS/FAIL) Endpoint theo resource; danh từ, số nhiều, version khi cần
- (PASS/FAIL) Dùng đúng HTTP verb/status; validate với `@Valid`
- (PASS/FAIL) Trả về DTO; không expose entity trực tiếp

Ví dụ:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public Page<UserDto> list(Pageable pageable) { /* ... */ return Page.empty(); }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        // ... tạo và trả 201 kèm Location header
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto());
    }
}
```

### 10. Testing
- (PASS/FAIL) Unit test cho service; slice test cho controller/repository
- (PASS/FAIL) Dùng testcontainers cho integration test với DB thật
- (PASS/FAIL) Test phải xác định, nhanh

Ví dụ:
```java
@SpringBootTest
class UserServiceTests {
    @Autowired UserService userService;
}
```

### 11. Đa ngôn ngữ (i18n)
- (PASS/FAIL) Dùng file messages và `MessageSource`
- (PASS/FAIL) Không hardcode text hiển thị trong code Java

Ví dụ:
```java
@Autowired MessageSource messageSource;
String text = messageSource.getMessage("user.created", null, locale);
```

### 12. Quyền riêng tư & dữ liệu nhạy cảm
- (PASS/FAIL) Không log/lưu plaintext passwords, token, secrets
- (PASS/FAIL) Mask PII trong log; tối thiểu hóa trường trả về trong API

### 13. Đồng thời & mở rộng (Concurrency & Scaling)
- (PASS/FAIL) Dùng optimistic locking (`@Version`) khi cần
- (PASS/FAIL) Service stateless; externalize session để scale

Ví dụ:
```java
@Entity
public class Account {
    @Version
    private long version;
}
```

### 14. Hỗ trợ UI/UX từ backend
- (PASS/FAIL) Phân trang, sắp xếp, lọc ở phía server
- (PASS/FAIL) Trả lỗi rõ ràng với mã máy đọc được

### 15. Version control & commit
- (PASS/FAIL) Message rõ ràng; mỗi commit một thay đổi logic
- (PASS/FAIL) Dùng feature branch và review PR

## II. Quy tắc Style cho Java (tham khảo Effective Java + Google Style)

### 1. Cấu trúc dự án
- Tách module khi phù hợp; phân lớp rõ: `controller`, `service`, `repository`, `model`, `config`, `security`, `dto`

### 2. Định dạng & đặt tên
- Thụt lề 4 khoảng trắng; không dùng tab
- Class: `PascalCase`; method/field: `camelCase`; constant: `UPPER_SNAKE_CASE`
- Độ dài dòng ưu tiên ≤ 120 ký tự

### 3. Kiểu dữ liệu & null-safety
- Dùng Optional hợp lý (trả về, không dùng cho field)
- Validate input; tránh chỉ dùng `@NotNull` mà không có kiểm tra runtime

### 4. Luồng điều khiển
- Ưu tiên early return; tránh lồng nhau sâu
- Không catch rỗng; không bắt `Exception` chung trừ khi ném lại

### 5. Comment
- Ngắn gọn, đúng mục đích; loại bỏ comment lỗi thời

### 6. Thư viện
- Ưu tiên thư viện ổn định (Spring, Jackson, MapStruct, Lombok thận trọng)
- Tránh phụ thuộc không cần thiết

### 7. Build & CI
- Build tái lập bằng Maven/Gradle; dùng dependency management
- Phân tích tĩnh: SpotBugs, Checkstyle; coverage trong CI

---

Phụ lục: Annotation hữu ích
- `@RestController`, `@Service`, `@Repository`, `@Component`
- `@Transactional`, `@Validated`, `@Value`, `@ConfigurationProperties`
- `@Slf4j`, `@RequiredArgsConstructor`, `@Builder`


<!-- 
Prompt:
Dựa vào file @cursorrules-php.md hãy review giúp tôi branch FN-1902
1. Hãy chấm điểm chất lượng code của tôi hiện tại
2. Nêu rõ những tiêu chí chưa đạt, phân tích những đoạn code có chất lượng thấp
3. Đưa ra những phương án cải thiện cụ thể cho những lỗi trên nếu có
4. Tổng kết lại phần review
-->
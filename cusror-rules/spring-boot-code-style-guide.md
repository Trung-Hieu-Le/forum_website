# 🌱 Spring Boot Code Style & Best Practices Guide 

This guide summarizes the **most essential and frequently used rules** for writing maintainable, secure, and scalable Java Spring Boot applications.

You are a senior software architect and code reviewer, specializing in Java Spring Boot. Please review the code snippet provided below based on the following detailed quality rules across six key categories.
---

## 🏛️ 1. Architecture Rules

- **Layered separation**: Organize code into clear layers: `controller`, `service`, `repository`, `model`.
- **SOLID principles**: Follow all SOLID principles (Single-responsibility, Open-closed, etc.).
- **Dependency inversion**: Inject abstractions (`interfaces`) into business logic, not implementations.
- **Avoid God classes**: Break large classes into smaller, focused components.
- **No cyclic dependencies**: Keep modules/packages acyclic.
- **Package by feature**: Group code by domain (e.g., `/user`, `/invoice`) instead of technical layer.

---

## 🔍 2. Query Optimization Rules

- **Avoid N+1 queries**: Use `@EntityGraph`, `JOIN FETCH`, or batch fetching.
- **Select only required columns**: Never use `SELECT *`.
- **Use pagination**: Apply `Pageable` for large result sets.
- **Index critical columns**: Ensure columns used in `WHERE`, `JOIN`, or `ORDER BY` are indexed.
- **Batch inserts/updates**: Use batch operations for performance.
- **Use parameter binding**: Prevent SQL injection and improve query caching.
- **Close resources**: Always close JDBC connections, ResultSets, or use try-with-resources.

---

## 🛡️ 3. Security Rules

- **Use HTTPS**: Enforce encrypted communication (TLS).
- **Validate all inputs**: Use `@Valid`, `@Pattern`, and custom validators.
- **Never expose entities**: Use DTOs for API input/output.
- **Avoid hardcoded secrets**: Store secrets in environment variables or secret managers.
- **Short-lived tokens**: Use short-expiry JWTs and refresh tokens securely.
- **Use `@PreAuthorize` or `@Secured`**: Secure methods with role-based access.
- **CSRF protection**: Enable CSRF tokens for state-changing operations.
- **Set security headers**: Add `X-Frame-Options`, `Content-Security-Policy`, etc.

---

## 🧪 4. Testing Rules

- **Unit test business logic**: Cover all public methods in service layer.
- **Use `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest`** appropriately.
- **Mock external dependencies**: Use Mockito or Testcontainers.
- **Test edge cases**: Include nulls, errors, and invalid inputs.
- **Run tests in CI**: All commits should trigger the test suite.
- **Avoid flaky tests**: Use `@DirtiesContext` carefully and clean shared states.
- **Test exception paths**: Ensure fallback and error logic is covered.

---

## 📘 5. Documentation Rules

- **Comment WHY, not WHAT**: Explain intent, not obvious code behavior.
- **Use Javadoc**: Add method-level docs for public APIs and libraries.
- **Generate OpenAPI/Swagger**: Describe REST endpoints automatically.
- **README.md**: Include setup, purpose, how to run/test/deploy.
- **Maintain CHANGELOG.md**: Follow semantic versioning and track releases.
- **Keep docs up to date**: Update as part of the same pull request with code changes.

---

## 🌿 6. Spring Boot Specific Rules

- **Use Starter dependencies**: Prefer `spring-boot-starter-*` libraries.
- **Externalize config**: Use `application.yml` or `application-{profile}.yml`.
- **Use `@ControllerAdvice`**: Handle exceptions globally with proper structure.
- **Don't autowire fields**: Use constructor injection (`@RequiredArgsConstructor`).
- **Actuator endpoints**: Enable health checks and metrics (`/actuator/*`).
- **Use DTO + `@Valid`**: Never bind HTTP request directly to entity classes.
- **Structure by package**:

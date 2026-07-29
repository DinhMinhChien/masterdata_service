# Coding Convention — SSV Microservice Template

Quy ước bắt buộc khi viết code trên template & các service clone từ nó.
Kiến trúc / vòng đời / luồng tích hợp xem [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 1. Quy ước đặt tên

**Package / module**
- Package: chữ thường, không gạch dưới — `vn.com.ssv.<service>.<layer>`. Mọi class phải nằm dưới package gốc (để component scan thấy).

**Class / interface**

| Loại         | Quy ước | Ví dụ                     |
|--------------|---|---------------------------|
| Class        | PascalCase, danh từ | `Application`              |
| Interface    | PascalCase | `ApplicationRepository`    |
| Impl         | hậu tố `Impl`, đặt trong `service/impl/` | `ApplicationServiceImpl`   |
| Controller (impl) | hậu tố `Controller` | `ApplicationController`    |
| API interface | hậu tố `Api` | `ApplicationApi`           |
| Service      | hậu tố `Service` | `ApplicationService`       |
| Repository   | hậu tố `Repository` | `ApplicationRepository`    |
| Entity       | danh từ số ít (= bảng) | `Application`              |
| Request DTO  | hậu tố `Request` | `CreateApplicationRequest` |
| Response DTO | hậu tố `Response` | `ApplicationResponse`      |
| Mapper       | hậu tố `Mapper` | `ApplicationMapper`        |
| Config       | hậu tố `Config` | `SecurityConfig`           |
| Properties   | hậu tố `Properties` | `InternalProperties`       |
| Exception    | hậu tố `Exception` | `BusinessException`        |
| HTTP client  | hậu tố `Client` | `AdminManagerClient`       |
| Test         | hậu tố `Test` | `ApplicationServiceTest`   |

**Method / biến / hằng**
- Method: camelCase, bắt đầu bằng động từ — `createApplication`, `findById`, `mapToResponse`.
- Method trả boolean: tiền tố `is/has/can` — `isActive`, `hasRole`.
- Biến / tham số: camelCase, danh từ rõ nghĩa — `applicationId` (tránh `id1`, `tmp`, `data2`).
- Biến collection (List/Set): số nhiều — `applications`, `roleIds`; Map: `<value>By<key>` — `userById`.
- Hằng số: `static final` UPPER_SNAKE — `MAX_RETRY`, `DEFAULT_PAGE_SIZE`.
- Enum: type PascalCase số ít, hằng UPPER_SNAKE — `Status.PENDING`.
- Generic type: 1 chữ hoa — `T`, `E`, `K`, `V`.

**REST endpoint (URL)**
- Danh từ số nhiều, kebab-case: `/applications`, `/application-types`.
- Tài nguyên lồng nhau: `/applications/{id}/documents`.
- KHÔNG động từ trong URL — dùng HTTP method: `POST /applications` (không `/createApplication`).
- Hành động ngoài CRUD: `POST /applications/{id}/approve`.

**DB (DB-first — theo schema có sẵn)**
- Bảng & cột: snake_case — `application_type`, `created_at`.
- Field entity camelCase, map qua `@Column(name = "...")`.

**Mã & key**
- `DomainCode`: `<PREFIX>-<số>`, PREFIX theo service (mẫu `SSV-xxx`).
- Permission: `resource:action` chữ thường — `application:create`.
- Config key (yaml): kebab-case — `keycloak.base-url` (bind sang field camelCase).

**Comment**
- Dùng `//` cho mọi comment (kể cả mô tả class/method) — KHÔNG dùng Javadoc `/** */`.
- Comment giải thích "tại sao", không mô tả lại "cái gì" đã rõ trong code.

---

## 2. Phân tầng (bắt buộc)
```
Controller  -> Service (interface) -> Repository
```
- **Controller**: chỉ nhận request, gọi service, trả `ApiResponse`. KHÔNG chứa nghiệp vụ.
- **Service** giữ logic + `@Transactional`. Controller KHÔNG gọi thẳng Repository.
- **Repository** chỉ truy vấn DB.
- Controller impl `Api` interface; endpoint + `@Operation` (Swagger) khai trong `Api` interface.

---

## 3. Response — luôn dùng `ApiResponse`
- `ApiResponse<T>` là GENERIC (`data` kiểu `T`). Khai kiểu cụ thể khi cần Jackson deserialize đúng — nhất là HTTP client: `ApiResponse<List<RolePermissionDto>>` (để `Object` sẽ ra `LinkedHashMap`).
- Mọi endpoint trả `ApiResponse` dựng qua `ApiResponseFactory` (`success`, `success(data)`, `of`, `error`). KHÔNG tự `new ApiResponse(...)` rải rác.
- Phân trang: trả `PageResponse<T>`.
- DRY: các hàm tiện ích gọi lại hàm gốc `of()`, không lặp builder.

---

## 4. Exception — ném DomainCode, KHÔNG tự ghép message
- Lỗi nghiệp vụ: `throw new BusinessException(DomainCode.X, args...)`. KHÔNG `throw new RuntimeException("chuỗi")`.
- KHÔNG tự nối chuỗi message ở chỗ ném lỗi — message nằm trong `DomainCode`, `args` điền vào `{}`.
- KHÔNG try/catch để nuốt lỗi ở controller — để `GlobalExceptionHandler` xử lý tập trung.
- Lưu ý 2 tầng bắt lỗi: `GlobalExceptionHandler` (`@RestControllerAdvice`) chỉ bắt lỗi trong dispatch controller. Lỗi auth tầng FILTER (401 thiếu/sai token, 403 theo URL) do `RestSecurityExceptionHandler` xử lý — xem [ARCHITECTURE.md](ARCHITECTURE.md) mục 3 (Chặng 5).

---

## 5. DomainCode
- Mỗi mã: `code` + `HttpStatus` + `message` (dùng `{}` cho tham số).
- Code dạng `<PREFIX>-<nhóm><số>`: `0xx` success, `4xx` lỗi client, `5xx` lỗi server. Đổi `PREFIX` theo service.
- Permission code (cho phân quyền) dạng `resource:action`, vd `application:create`.

---

## 6. Phân quyền
- Action-level — ưu tiên dùng meta-annotation cho gọn (template `{value}` bật qua bean `AnnotationTemplateExpressionDefaults` trong `MethodSecurityConfig`):
  - 1 quyền: `@RequiresPermission("application:create")`
  - ÍT NHẤT 1 (OR): `@RequiresAnyPermission({"application:create", "application:update"})`
  - TẤT CẢ (AND): `@RequiresAllPermission({"application:read", "application:export"})`
  - tương đương `@PreAuthorize("@permissionChecker.has/hasAny/hasAll(...)")` nếu muốn viết trực tiếp.
- Permission code KHÔNG chứa dấu phẩy (`,` là separator của @RequiresAny/AllPermission) — quy ước `resource:action` an toàn.
- Object-level (`hasPermission(#id,...)` -> `CustomPermissionEvaluator`): **điểm mở rộng, mặc định CHƯA dùng** (stub return true).
- Ràng buộc theo bản ghi (role, user, theo chi nhánh, theo trạng thái,...) -> viết THẲNG trong service (throw `BusinessException`), KHÔNG bắt buộc dùng object-level.
- Cô lập dữ liệu (chi nhánh A không thấy data B) -> lọc ở QUERY (`where branch = ...`), KHÔNG ở annotation.
- Role lấy từ JWT (Keycloak); permission map từ role (Admin Manager, cache trong `RolePermissionCache`). KHÔNG quản user/role/permission trong service nghiệp vụ.

---

## 7. DTO & Mapper
- Request/Response DTO tách riêng (`feature/model/request`, `feature/model/response`). KHÔNG trả entity ra controller.
- Map entity <-> DTO bằng **MapStruct** (`mapper/`), không map tay.
- Validate input bằng annotation (`@NotNull`, `@Valid`...); custom rule -> `common/validator/`.

---

## 8. Gọi service khác
- Dùng **`@HttpExchange`** (interface) trong `feature/client/`, auto đăng ký qua `@ImportHttpServices`.
- 1 group = 1 base URL; client chung URL -> chung base-package -> cùng group.
- Token user + traceId được propagate qua interceptor trong `HttpClientConfig` — KHÔNG tự set header Authorization bằng tay.

---

## 9. DB-first / Entity
- App KHÔNG tạo/sửa schema: `ddl-auto: validate` (hoặc `none`).
- Entity map vào bảng có sẵn; kế thừa `BaseEntity` để có `created_by/at`, `updated_by/at` (auditing tự set qua `JpaAuditingConfig`).
---

## 10. Logging
- Dùng `@Slf4j` (Lombok) + SLF4J. KHÔNG `System.out.println`.
- Log có sẵn `requestId/username/traceId` (MDC + Micrometer). Log tham số bằng `{}`: `log.info("x={}", x)`, không nối chuỗi.
- Log expected error (lỗi nghiệp vụ) bằng `log.warn`; lỗi không lường (`Exception`) bằng `log.error(..., e)` kèm stack trace.
- KHÔNG log thông tin nhạy cảm (token, mật khẩu, PII).
- AOP `LogExecutionAspect` tự log request/response controller; payload bị cắt theo `app.log.max-payload-length`.

---

## 11. Config & secret
- Mọi giá trị môi trường đọc qua `${ENV:default}`. Production: override bằng env / K8s Secret.
- KHÔNG hardcode secret thật vào file commit lên git (môi trường test là ngoại lệ tạm thời).

---

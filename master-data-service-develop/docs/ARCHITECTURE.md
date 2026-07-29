# Kiến trúc & Vòng đời — SSV Microservice Admin Service

Template để clone ra các backend service của platform SSV.

**Stack:** Java 21 · Spring Boot 4.0.x · Spring Framework 7 · `@HttpExchange` · Keycloak (OAuth2 Resource Server) · Micrometer Tracing + OpenTelemetry · JPA **DB-first** · PostgreSQL.

> Quy ước đặt tên / phân tầng / DTO... xem [CODING_CONVENTION.md](CODING_CONVENTION.md).

---

## 1. Cấu trúc thư mục

```
src/main/java/vn/com/ssv/admin/
│
├── AdminApplication.java               // @SpringBootApplication — điểm vào, khởi động cả app
│
├── common/                                // HẠ TẦNG dùng lại — clone xong hạn chế sửa
│   │
│   ├── config/                            // cấu hình hạ tầng (bean, security)
│   │   ├── SecurityConfig.java            // SecurityFilterChain: public endpoints, bật/tắt auth, gắn Resource Server
│   │   ├── MethodSecurityConfig.java      // gắn CustomPermissionEvaluator + bật template '{value}' cho @RequiresPermission
│   │   └── JacksonConfig.java             // trim string (+ ""->null) + format LocalDate/LocalDateTime (Jackson 3)
│   │
│   ├── security/                          // xác thực (authentication) & phân quyền (authorization)
│   │   ├── KeycloakJwtAuthenticationConverter.java  // JWT -> Authentication: roles + permission thành GrantedAuthority
│   │   ├── PermissionResolver.java        // interface: đổi tập role -> tập permission
│   │   ├── RolePermissionCache.java       // impl PermissionResolver: cache role->permission; nạp lúc start + định kỳ (app.admin.refresh-ms) + lazy khi request có role lạ (throttle) từ Admin Manager
│   │   ├── PermissionChecker.java         // bean cho @PreAuthorize: has() / hasAny() / hasAll() — check action-level
│   │   ├── CustomPermissionEvaluator.java // check object-level: hasPermission(#id,'Type','action') — stub return true
│   │   ├── RestSecurityExceptionHandler.java // 401/403 tầng filter -> trả ApiResponse JSON (entryPoint + accessDeniedHandler)
│   │   └── RequiresPermission / RequiresAnyPermission / RequiresAllPermission  // meta-annotation gọn thay @PreAuthorize
│   │
│   ├── response/                          // chuẩn hoá dữ liệu trả ra
│   │   ├── ApiResponse.java               // khuôn response GENERIC ApiResponse<T>: {transactionTime, code, message, data:T, traceId}
│   │   ├── ApiResponseFactory.java        // dựng ApiResponse: format message {} + gắn traceId
│   │   ├── PageResponse.java              // khuôn dữ liệu phân trang {content, page, size, total...}
│   │   └── DomainCode.java                // danh mục mã: code + HttpStatus + message
│   │
│   ├── exception/                         // xử lý lỗi tập trung
│   │   ├── BaseException.java             // exception nền: mang DomainCode + args
│   │   ├── BusinessException.java         // lỗi nghiệp vụ
│   │   ├── ResourceNotFoundException.java // lỗi không tìm thấy -> 404
│   │   └── GlobalExceptionHandler.java    // @RestControllerAdvice: đổi mọi exception -> ApiResponse + HTTP status
│   │
│   ├── observability/                     // log & trace
│   │   ├── MdcLoggingFilter.java          // bơm requestId/username vào MDC mỗi request (để log có context)
│   │   └── Logger.java                    // tiện ích log thống nhất
│   │
│   ├── aop/                               // cross-cutting
│   │   ├── LogExecution.java              // annotation đánh dấu method cần log
│   │   └── LogExecutionAspect.java        // aspect log request/response + thời gian (cần aspectjweaver)
│   │
│   ├── httpclient/                        // gọi service khác (xem mục 4)
│   │   ├── HttpClientConfig.java          // @ImportHttpServices + RestClient group + interceptor token/traceId
│   │   └── InternalProperties.java        // bind app.internal.* (base-url gateway nội bộ)
│   │
│   ├── persistence/                       // nền JPA
│   │   ├── BaseEntity.java                // @MappedSuperclass: created_by/at, updated_by/at (audit)
│   │   ├── BaseRepository.java            // @NoRepositoryBean: findByIdOrThrow,...
│   │   ├── NativeQuerySupport.java        // helper native query + phân trang (JdbcTemplate)
│   │   ├── JpaAuditingConfig.java         // AuditorAware: cấp createdBy/updatedBy từ user hiện tại
│   │   └── dto/                           // DTO hạ tầng: BaseDto, PageDto (page/size/search), RolePermissionDto
│   │
│   ├── validator/                         // custom Bean Validation
│   │   ├── AtLeastOne / MaxLengthAdvanced / MinOneMaxDigits (+ *Validator)
│   │
│   └── util/                              // tiện ích static (không state)
│       ├── SecurityUtil.java             // đọc user hiện tại (id, roles, permissions, token) từ SecurityContext
│       ├── StringUtil.java               // mask PII, normalize chuỗi
│       ├── DateUtil.java                 // format/parse ngày giờ (Asia/Ho_Chi_Minh)
│       └── Const.java                    // hằng số dùng chung
│
└── feature/                      // 1 NGHIỆP VỤ = code ở đây (mẫu: Application)
    ├── controller/
    │   ├── api/ApplicationApi.java         // interface: khai báo endpoint + @Operation (Swagger)
    │   └── ApplicationController.java       // impl ApplicationApi: nhận request, gọi service, trả ApiResponse
    ├── service/
    │   ├── ApplicationService.java          // interface nghiệp vụ
    │   └── impl/ApplicationServiceImpl.java // impl: logic + @Transactional
    ├── repository/
    │   ├── ApplicationRepository.java       // JpaRepository
    │   └── custom/ApplicationRepositoryCustom.java (+ impl/) // native query tuỳ biến
    ├── entity/
    │   └── Application.java                 // @Entity map bảng có sẵn, extends BaseEntity
    ├── model/
    │   ├── dto                              
    │   ├── request/CreateApplicationRequest.java  
    │   └── response/ApplicationResponse.java 
    ├── mapper/
    │   └── ApplicationMapper.java           // MapStruct: map entity <-> model
    └── client/
        └── AdminManagerClient.java          // @HttpExchange: gọi Admin Manager lấy map role->permission
```

Nguyên tắc: **`common/` là khung dùng lại; nghiệp vụ chỉ viết trong `feature/`.**

---

## 2. Vòng đời ứng dụng

### 2.1 Khởi động (startup) — lắp ráp 1 lần
```
main() -> SpringApplication.run()
  1. Đọc config:  application.yml + application-<profile>.yml   (profile từ ${ENVIRONMENT:local})
  2. Tạo ApplicationContext (cái "thùng" chứa toàn bộ bean)
  3. Component scan từ package gốc vn.com.ssv.master_data -> nhặt @Component/@Service/@RestController/@Configuration
  4. Tạo bean theo thứ tự phụ thuộc + inject lẫn nhau + chạy @PostConstruct (vd RolePermissionCache.reload())
  5. AutoConfiguration dựng sẵn: DataSource, JPA, Security (Resource Server), Actuator, Tracing
  6. Start Tomcat (cổng 8080, context-path /<service>)
  7. ApplicationReadyEvent -> in "Started AdminApplication" -> đứng chờ request
```
Sau bước 7, mọi bean sống suốt vòng đời app (singleton).

### 2.2 Vòng đời 1 request
```
[1] HTTP request + Bearer JWT  -> Tomcat

[2] FILTER CHAIN (chạy TRƯỚC controller):
      MdcLoggingFilter                      // gắn requestId/username vào MDC -> mọi dòng log có context
      Security filters                      // verify chữ ký JWT bằng khóa công khai Keycloak (JWKS)
        -> KeycloakJwtAuthenticationConverter  // đọc roles + permission, đặt Authentication vào SecurityContext

[3] DispatcherServlet                       // map URL -> đúng Controller method

[4] CHECK QUYỀN  @PreAuthorize / @RequiresPermission
        -> PermissionChecker.has(...)  hoặc  CustomPermissionEvaluator.hasPermission(...)
        sai -> ném AccessDeniedException (nhảy xuống [E])

[5] Controller -> Service (@Transactional) -> Repository -> DB

[6] data trả về -> ApiResponseFactory.success(data) -> ApiResponse

[7] Jackson 3 serialize ApiResponse -> JSON -> client

[8] finally: MDC.clear()                    // dọn context, request sau không dính traceId cũ
```
Điểm cốt lõi: filter/quyền/log chạy **trước** controller -> controller luôn nhận request đã "sạch".

### 2.3 Vòng đời lỗi (exception)
```
[E] Bất kỳ đâu: vd throw new BusinessException(DomainCode.NOT_FOUND, "id 5")
      -> GlobalExceptionHandler (@RestControllerAdvice) bắt
      -> ApiResponseFactory.of(code, null, args)   // format message + gắn traceId
      -> ResponseEntity.status(code.getHttpStatus()).body(apiResponse)   // vd 404
```
Service chỉ cần `throw` — không cần biết trả status gì hay dựng JSON lỗi; một chỗ lo hết -> lỗi luôn cùng format.

---

## 3. Tích hợp xác thực: FE ↔ Keycloak ↔ BE ↔ BE khác

Tổng quan 4 chặng. **Mấu chốt: BE KHÔNG hỏi Keycloak ở mỗi request — nó tự verify chữ ký JWT offline bằng public key đã cache.**

```
                    ┌─────────────────────────────────────────────┐
                    │              Keycloak (realm ssvn)           │
                    │  - quản lý user + login                      │
                    │  - ký token bằng PRIVATE KEY                 │
                    │  - publish PUBLIC KEY ở /certs (JWKS)        │
                    └───────▲──────────────────────┬──────────────┘
        (1) login           │                      │ (3) tải public key 1 lần rồi cache
        Auth Code + PKCE    │                      │
                            │                      │
         ┌──────────────────┴───┐         ┌────────┴───────────────┐
         │        FE (SPA)      │  (2)    │          BE             │
         │  public client       ├────────►│  Resource Server        │
         │  ssvn-platform-...    │ Bearer  │  (verify chữ ký offline)│
         └──────────────────────┘ token   └────────┬────────────────┘
                                                    │ (4) client_credentials
                                                    │     confidential client
                                                    ▼
                                              ┌──────────────┐
                                              │ BE khác      │
                                              │ (Admin Mgr)  │
                                              └──────────────┘
```

### Chặng 1 — FE login vào Keycloak (Authorization Code + PKCE)
FE dùng **public client** `ssvn-platform-client-id`:
```
1. User bấm Login -> FE redirect sang Keycloak (kèm code_challenge)
2. User nhập user/pass TRÊN trang Keycloak (FE không thấy mật khẩu)
3. Keycloak redirect về FE kèm "authorization code"
4. FE đổi code lấy token (kèm code_verifier chứng minh)
5. Keycloak trả:  access_token (JWT, ~5')  +  refresh_token (dài)  +  id_token
```
`access_token` là JWT `header.payload.signature`, payload chứa:
```json
{
  "sub": "uuid-user",
  "preferred_username": "huydq2",
  "realm_access": { "roles": ["ADMIN","USER"] },   
  "exp": 1718000000,
  "iss": "https://id.smartsolutionvn.com.vn/realms/ssvn"
}
```
`signature` ký bằng **private key** Keycloak.

### Chặng 2 — FE gọi BE
Mỗi request đính header:
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...
```

### Chặng 3 — BE verify token thế nào (KHÔNG gọi Keycloak mỗi request)
```
Lúc khởi động / lần đầu gặp token:
  BE gọi 1 lần: GET ${keycloak.base-url}/realms/ssvn/protocol/openid-connect/certs
  -> tải PUBLIC KEY (JWKS) về CACHE trong RAM.

Mỗi request sau đó (offline, không gọi Keycloak):
  1. lấy chuỗi sau "Bearer "
  2. dùng public key đã cache -> verify chữ ký   (token có bị sửa không?)
  3. check exp                                    (còn hạn?)
  4. check iss                                    (đúng issuer realm ssvn?)
  pass -> tạo Authentication, đẩy vào SecurityContext
  fail -> 401
```
Đây là vai trò config trong yaml:
```yaml
resourceserver.jwt:
  jwk-set-uri: .../certs          # nơi tải public key (verify chữ ký)
  issuer-uri:  .../realms/ssvn     # check claim iss
```
Vì verify bằng public key offline -> BE **stateless**, không session, không phụ thuộc Keycloak sống/chết ở mỗi request. `client-id/secret` **không liên quan** chặng này.

Chi tiết pipeline Spring Security:
```
Request: Authorization: Bearer eyJ...
  -> BearerTokenAuthenticationFilter      // cắt "Bearer ", bọc token CHƯA xác thực
  -> JwtAuthenticationProvider
       A. NimbusJwtDecoder.decode()        // dùng JWKS cache -> verify chữ ký + exp + iss -> object Jwt
                                           //   fail -> AuthenticationException -> 401
       B. KeycloakJwtAuthenticationConverter.convert(jwt)
                                           //   realm_access.roles -> ROLE_* + permissionResolver.permissionsOf(roles)
                                           //   -> new JwtAuthenticationToken(jwt, authorities)
  -> set vào SecurityContext
  -> downstream: @PreAuthorize, SecurityUtil.getJwt()/getCurrentUsername()
```

| Bước | Ai làm | Thành phần |
|---|---|---|
| Cắt "Bearer " | framework | `BearerTokenResolver` |
| Verify chữ ký + exp + iss | framework | `NimbusJwtDecoder` (qua `jwk-set-uri`) |
| Claim -> authorities | **TA** | `KeycloakJwtAuthenticationConverter` |
| Set SecurityContext | framework | `BearerTokenAuthenticationFilter` |

### Chặng 4 — BE gọi BE khác (không có user: job/startup/kafka)
Khi không có token user để "mượn" (vd `RolePermissionCache.reload()` gọi Admin Manager), BE tự đăng nhập bằng **client_credentials** qua client **confidential** riêng:
```
BE -> Keycloak: POST /token
      grant_type=client_credentials
      client_id=ssvn-template-service     (confidential, Service accounts = On)
      client_secret=...
Keycloak -> access_token đại diện SERVICE (sub = service account, không phải người)
BE -> gắn token đó vào request đi Admin Manager
```
Logic ưu tiên trong `HttpClientConfig.authInterceptor`:
```
có token user (request do người kích hoạt) -> mang token user đi tiếp   (giữ đúng danh tính)
không có (job/startup)                       -> dùng client_credentials   (đi bằng danh nghĩa service)
```

> **Lưu ý cấu hình Keycloak:** client dùng cho `client_credentials` PHẢI là **confidential** + bật **Service accounts**. Dùng public client sẽ lỗi `401 unauthorized_client: "Public client not allowed to retrieve service account"`. Nên tách riêng client FE (public) và client BE service-to-service (confidential), cùng realm `ssvn` -> cùng issuer -> BE verify được cả hai bằng đúng `jwk-set-uri`.

### Chặng 5 — Phân quyền tầng method (`@PreAuthorize` / `@RequiresPermission`)

Quan trọng: permission được **nạp sẵn** vào SecurityContext lúc verify JWT (Chặng 3); annotation chỉ **đọc lại** để so — KHÔNG gọi DB/Keycloak lúc check.

```
LÚC LOGIN (1 lần/request, ở filter)          LÚC GỌI METHOD (mỗi lần gọi)
KeycloakJwtAuthenticationConverter           @PreAuthorize -> PermissionChecker
nạp [ROLE_*, application:create, ...]         đọc lại tập trên rồi so
vào SecurityContext
```

Luồng khi gọi 1 method có annotation:
```
gọi applicationController.create(req)
   │  proxy AOP của @EnableMethodSecurity chặn TRƯỚC khi vào thân hàm
   ▼
AuthorizationManager (PreAuthorizeAuthorizationManager)
   │  lấy chuỗi SpEL: "@permissionChecker.has('application:create')"
   ▼
SpEL engine:
   - @permissionChecker  -> tra bean tên "permissionChecker"
   - .has('application:create')
        -> getPermissions()  // đọc authorities từ SecurityContext (đã nạp ở Chặng 3)
           .contains("application:create")  -> true / false
   ▼
   true  -> chạy thân method
   false -> ném AccessDeniedException -> GlobalExceptionHandler -> 403
```

`@RequiresPermission` thêm 1 bước convert template trước khi tới AuthorizationManager:
```
@RequiresPermission("application:create")
   │  bean AnnotationTemplateExpressionDefaults thay {value}
   ▼  @PreAuthorize("@permissionChecker.has('application:create')")  ... rồi chạy như trên
@RequiresAnyPermission({"a","b"}) -> template nối "a,b" -> SpEL hasAny('a,b'.split(',')) -> hasAny("a","b")
```

| Thành phần | Vai trò |
|---|---|
| `KeycloakJwtAuthenticationConverter` | **nạp** quyền vào SecurityContext (1 lần/request) |
| `@EnableMethodSecurity` | bật proxy AOP chặn method có annotation |
| `@PreAuthorize` / `@RequiresPermission` | **khai báo** điều kiện (chuỗi SpEL) |
| `AnnotationTemplateExpressionDefaults` | dịch `{value}` trong meta-annotation |
| SpEL engine | chạy biểu thức, gọi bean `@permissionChecker` |
| `PermissionChecker.has/hasAny/hasAll` | **đọc** quyền trong SecurityContext rồi so -> boolean |

### Debug khi gặp 403 lạ — thứ tự kiểm tra
```
1. Breakpoint PermissionChecker.has()  -> getPermissions() có chứa quyền cần không?
       CÓ   -> lỗi KHÔNG ở phân quyền (chỗ khác)
       KHÔNG ↓
2. Breakpoint cuối KeycloakJwtAuthenticationConverter.convert() -> JWT nạp đủ role/permission chưa?
       Thiếu -> lỗi ở RolePermissionCache / converter / token Keycloak
       Đủ    -> lỗi ở chuỗi SpEL annotation ↓
3. Breakpoint PreAuthorizeAuthorizationManager#check (Ctrl+N mở class framework)
       -> xem expression CUỐI CÙNG (template đã thay {value} đúng chưa?)[repository](../src/main/java/vn/com/ssv/admin/feature/repository)
Bật log để thấy toàn luồng authorize (thêm vào `application-local.yml`):
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.authorization: TRACE
```
Class framework hữu ích để đặt breakpoint: `PreAuthorizeAuthorizationManager#check`, `DefaultMethodSecurityExpressionHandler#createEvaluationContext` (nơi gắn bean `@permissionChecker` vào SpEL), `ExpressionTemplateSecurityAnnotationScanner` (xem `{value}` được thay thành gì).

### Lỗi auth tầng FILTER (401 / 403-URL) — KHÔNG qua `@RestControllerAdvice`
Token thiếu/sai/hết hạn bị chặn trong **filter chain** (`BearerTokenAuthenticationFilter`) — TRƯỚC `DispatcherServlet`, nên `GlobalExceptionHandler` (`@RestControllerAdvice`) **không bắt được**. Mặc định Resource Server chỉ trả header `WWW-Authenticate` + body rỗng.
-> Template cắm `RestSecurityExceptionHandler` (impl `AuthenticationEntryPoint` + `AccessDeniedHandler`) vào `SecurityConfig` để 401/403 tầng filter cũng trả `ApiResponse` JSON đồng nhất.

| Loại lỗi | Bị chặn ở đâu | Ai trả ApiResponse |
|---|---|---|
| 401 thiếu/sai token | filter (trước controller) | `RestSecurityExceptionHandler.commence` |
| 403 thiếu quyền theo URL (`authorizeHttpRequests`) | filter | `RestSecurityExceptionHandler.handle` |
| 403 thiếu quyền theo method (`@PreAuthorize`) | trong dispatch controller | `GlobalExceptionHandler.handleAccessDenied` |
| lỗi nghiệp vụ / validate / 500 | trong dispatch controller | `GlobalExceptionHandler` |

---

## 4. HttpClient — gọi service khác bằng `@HttpExchange`

Dùng `@HttpExchange` (interface khai báo) + `RestClient` (transport), auto đăng ký bằng `@ImportHttpServices`.

### Cơ chế
```
@ImportHttpServices(group="internal", basePackages="...feature.client")
   -> Spring quét các interface @HttpExchange trong package -> tạo PROXY bean cho mỗi interface
   -> inject thẳng vào nơi cần (vd RolePermissionCache)

RestClientHttpServiceGroupConfigurer (trong HttpClientConfig):
   forEachClient((group, builder) -> {
       builder.observationRegistry(...)        // propagate traceId (header traceparent)
       builder.requestInterceptor(authInterceptor)  // gắn Bearer token
       baseUrl = switch(group.name()) {        // 1 GROUP = 1 BASE URL
           case "internal" -> internalProperties.getBaseUrl();
       }
   })
```

### Quy tắc nhóm theo base URL
- **1 group = 1 base URL.** Các client chung URL -> để chung 1 base-package -> cùng group.
- Service URL khác -> thêm `@ImportHttpServices(group="external", basePackages="...")` + 1 nhánh `case "external"` trong switch baseUrl.

### Khai báo 1 client
```java
@HttpExchange("${service.admin-service.context-path:admin}")
public interface AdminManagerClient {
    @GetExchange("${service.admin-service.endpoints.get-permission:/api/v1/role-permission/all}")
    ApiResponse<List<RolePermissionDto>> getAllRolePermissions();
}
```
URL có thể dùng placeholder `${...}` (resolve lúc tạo proxy); nên có default `:<giá-trị>` để boot được cả khi profile khác chưa khai property.

---

## 5. Build & Run

Yêu cầu: **JDK 21** (PATH mặc định có thể là JDK 17 -> set `JAVA_HOME` về 21).

```
mvn spring-boot:run                       # profile local (mặc định, zero-infra)
# hoặc chạy jar:
java -jar target/*.jar --spring.profiles.active=local
```
Health: `GET http://localhost:8080/admin/actuator/health` -> `{"status":"UP"}`.

API mẫu (feature Application):
| Method | URL | Mô tả |
|---|---|---|
| POST | `/admin/applications/create` | tạo bản ghi demo (body `CreateApplicationRequest`) |
| GET  | `/admin/applications/{id}` | lấy 1 bản ghi |
| POST | `/admin/applications/search` | tìm kiếm phân trang (body `PageDto`: page/size/search) |

---

## 6. Clone tạo service mới

1. Copy thư mục, đổi `artifactId` + `name` trong `pom.xml`.
2. Đổi package `vn.com.ssv.master_data` -> `vn.com.ssv.<service>`.
3. Đổi `spring.application.name`, context-path, prefix `DomainCode`.
4. Đổi `client-id/secret` (Keycloak confidential client của service mới), `service.admin-service.*`.
5. Xoá `feature/` mẫu, tạo module nghiệp vụ mới. Giữ nguyên `common/`.

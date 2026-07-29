# MASTER_DATA_SERVICE — SSV Microservice Template

Dịch vụ quản trị của platform SSV.

**Stack:** Java 21 · Spring Boot 4.0.x · Spring Framework 7 · `@HttpExchange` · Keycloak (OAuth2 Resource Server) · Micrometer Tracing + OpenTelemetry · JPA **DB-first** · PostgreSQL.

## Tài liệu

- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — cấu trúc thư mục, vòng đời app (startup / request / exception), luồng tích hợp FE ↔ Keycloak ↔ BE ↔ BE khác, cơ chế verify JWT, HttpClient `@HttpExchange`, build & run, clone service mới.
- **[docs/CODING_CONVENTION.md](docs/CODING_CONVENTION.md)** — quy ước đặt tên, phân tầng, response/exception/DomainCode, phân quyền, DTO/Mapper, logging.

## Chạy nhanh

Yêu cầu **JDK 21**.
```
mvn spring-boot:run                                   # profile local (zero-infra)
# hoặc:
java -jar target/*.jar --spring.profiles.active=local
```
Health: `GET http://localhost:8080/master-data/actuator/health` → `{"status":"UP"}`.

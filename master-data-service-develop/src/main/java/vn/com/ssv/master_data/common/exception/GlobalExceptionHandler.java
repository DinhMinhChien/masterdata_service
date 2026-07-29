package vn.com.ssv.master_data.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.common.response.ApiResponseFactory;
import vn.com.ssv.master_data.common.response.DomainCode;

import java.util.LinkedHashMap;
import java.util.Map;

// Bắt mọi exception -> trả ApiResponse chuẩn theo DomainCode.httpStatus
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ApiResponseFactory responseFactory;

    // lỗi nghiệp vụ (BusinessException, ResourceNotFoundException...)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBusiness(BaseException e) {
        log.warn("handleBusiness: {}", e.getMessage());
        ApiResponse<?> body = responseFactory.error(e.getDomainCode(), e.getArgs());
        return ResponseEntity
                .status(e.getDomainCode().getHttpStatus())
                .body(body);
    }

    // 403 - không đủ quyền
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        log.warn("handleAccessDenied: {}", e.getMessage());
        ApiResponse<?> body = responseFactory.error(DomainCode.FORBIDDEN);
        return ResponseEntity
                .status(DomainCode.FORBIDDEN.getHttpStatus())
                .body(body);
    }

    // 401 - chưa xác thực
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuth(AuthenticationException e) {
        log.warn("handleAuth: {}", e.getMessage());
        ApiResponse<?> body = responseFactory.error(DomainCode.UNAUTHORIZED);
        return ResponseEntity
                .status(DomainCode.UNAUTHORIZED.getHttpStatus())
                .body(body);
    }

    // 400 - validate @Valid body. data = {field: lý do}, {} trong message = danh sách field sai
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        log.warn("handleValidation: {}", fieldErrors);
        ApiResponse<?> body = responseFactory.of(DomainCode.VALIDATION_ERROR, fieldErrors,
                String.join(", ", fieldErrors.keySet()));
        return ResponseEntity
                .status(DomainCode.VALIDATION_ERROR.getHttpStatus())
                .body(body);
    }

    // 400 - validate @RequestParam/@PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        e.getConstraintViolations()
                .forEach(v -> fieldErrors.put(v.getPropertyPath().toString(), v.getMessage()));
        log.warn("handleConstraintViolation: {}", fieldErrors);
        ApiResponse<?> body = responseFactory.of(DomainCode.VALIDATION_ERROR, fieldErrors,
                String.join(", ", fieldErrors.keySet()));
        return ResponseEntity
                .status(DomainCode.VALIDATION_ERROR.getHttpStatus())
                .body(body);
    }

    // 500 - fallback cho lỗi KHÔNG lường (bug/NPE...) -> log.error kèm stack trace, KHÔNG lộ chi tiết cho client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAll(Exception e) {
        log.error("Unhandled exception", e);
        ApiResponse<?> body = responseFactory.error(DomainCode.INTERNAL_ERROR);
        return ResponseEntity
                .status(DomainCode.INTERNAL_ERROR.getHttpStatus())
                .body(body);
    }
}

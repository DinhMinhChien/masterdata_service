package vn.com.ssv.master_data.common.response;

import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// Tạo ApiResponse chuẩn từ DomainCode (format message {}, gắn traceId)
@Component
public class ApiResponseFactory {

    public <T> ApiResponse<T> of(DomainCode code, T data, Object... args) {
        return ApiResponse.<T>builder()
                .transactionTime(LocalDateTime.now())
                .code(code.getCode())
                .message(formatMessage(code.getMessage(), args))
                .traceId(currentTraceId())
                .data(data)
                .build();
    }

    public ApiResponse<Void> success() {
        return of(DomainCode.SUCCESS, null, (Object) null);
    }

    public <T> ApiResponse<T> success(T data) {
        return of(DomainCode.SUCCESS, data, (Object) null);
    }

    public ApiResponse<Void> error(DomainCode code) {
        return of(code, null, (Object) null);
    }

    public ApiResponse<Void> error(DomainCode code, Object... args) {
        return of(code, null, args);
    }

    // ghép message
    private String formatMessage(String template, Object... args) {
        if (args == null || args.length == 0) {
            return template;                                       // không có args -> giữ nguyên khuôn
        }
        return MessageFormatter.arrayFormat(template, args).getMessage();
    }

    private String currentTraceId() {
        return MDC.get("traceId");
    }
}

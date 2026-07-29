package vn.com.ssv.master_data.common.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.ssv.master_data.common.util.SecurityUtil;

import java.io.IOException;
import java.util.UUID;

import static vn.com.ssv.master_data.common.util.Const.*;
git
// Bơm requestId + userId vào MDC -> mọi dòng log trong request tự có context.
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // requestId: ưu tiên header (gateway truyền xuống), không có thì tự sinh
            String requestId = request.getHeader(HEADER_REQUEST_ID);
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(REQUEST_ID, requestId);

            // userId: chỉ có khi đã qua security (request kèm token hợp lệ)
            String username = SecurityUtil.getCurrentUsername();
            if (username != null) {
                MDC.put(USERNAME, username);
            }
            // trả client để đối chiếu log
            response.setHeader(HEADER_REQUEST_ID, requestId);

            filterChain.doFilter(request, response);
        } finally {
            // Tomcat tái dùng thread ->  clear để không rò context sang request sau
            MDC.clear();
        }
    }
}

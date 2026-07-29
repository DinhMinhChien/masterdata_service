package vn.com.ssv.master_data.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;
import vn.com.ssv.master_data.common.util.SecurityUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

// AOP: tự log [REQUEST] (body) + [RESPONSE] (body) + thời gian cho mọi controller method (và method gắn @LogExecution).
// Body được JSON-hoá + CẮT theo giới hạn ký tự để log không phình to.
@Aspect
@Component
@RequiredArgsConstructor
public class LogExecutionAspect {

    private final ObjectMapper objectMapper;

    // giới hạn ký tự mỗi body log (cấu hình qua yaml: app.log.max-payload-length)
    @Value("${app.log.max-payload-length:1000}")
    private int maxPayloadLength;

    // pointcut: method gắn @LogExecution HOẶC mọi class trong ...controller...
    @Around("@annotation(vn.com.ssv.master_data.common.aop.LogExecution) "
            + "|| within(vn.com.ssv.master_data.feature.controller..*)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // log dưới tên class thật (vd ApplicationController) cho dễ truy vết
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());

        String http = currentRequest()
                .map(r -> r.getMethod() + " " + r.getRequestURI())
                .orElse(method.getName());

        logger.info("[REQUEST] {} - actor={} - body={}",
                http, SecurityUtil.getCurrentUsername(), truncate(argsToJson(joinPoint.getArgs())));
        try {
            Object result = joinPoint.proceed();
            logger.info("[RESPONSE] {} - {}ms - body={}",
                    http, System.currentTimeMillis() - start, truncate(toJson(result)));
            return result;
        } catch (Throwable ex) {
            // chỉ ghi nhận có lỗi + thời gian; chi tiết exception để GlobalExceptionHandler log
            logger.warn("[RESPONSE-ERR] {} - {}ms - {}", http, System.currentTimeMillis() - start, ex.toString());
            throw ex;
        }
    }

    // lấy HttpServletRequest hiện tại (nếu đang trong request)
    private Optional<HttpServletRequest> currentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    // JSON-hoá từng arg riêng -> 1 arg lỗi (vd MultipartFile) không làm hỏng cả dòng log
    private String argsToJson(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args).map(this::toJson).collect(Collectors.joining(", ", "[", "]"));
    }

    // JSON-hoá an toàn: không serialize được -> dùng String.valueOf (không ném lỗi ra ngoài luồng nghiệp vụ)
    private String toJson(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

    // cắt bớt nếu vượt giới hạn -> tránh log khổng lồ
    private String truncate(String s) {
        if (s == null || s.length() <= maxPayloadLength) {
            return s;
        }
        return s.substring(0, maxPayloadLength) + "...(" + s.length() + " ký tự, đã cắt)";
    }
}

package vn.com.ssv.master_data.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import vn.com.ssv.master_data.common.security.CustomPermissionEvaluator;

// Mở rộng nếu cần check theo user- object ( user này có được action với bản ghi này không)
// Hiện tại các logic check này sẽ được xử lý ở service nếu có
// MethodSecurityConfig — cắm CustomPermissionEvaluator vào tầng METHOD security.
// Nhờ vậy biểu thức hasPermission(...) trong @PreAuthorize chạy đúng logic của đã define (kiểm tra cấp object).
@Configuration
public class MethodSecurityConfig {

    // DefaultMethodSecurityExpressionHandler là cái ĐÁNH GIÁ biểu thức SpEL trong @PreAuthorize
    // (hasRole, hasAuthority, hasPermission...). Mặc định nó KHÔNG biết hasPermission xử lý ra sao
    // Để static giúp Spring tạo nó mà KHÔNG phải khởi tạo cả class @Configuration quá sớm
    // -> tránh lỗi lệch thứ tự khởi tạo. Đây là pattern khuyến nghị của Spring cho bean loại này.
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(CustomPermissionEvaluator evaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }

    // Bật template '{value}' trong meta-annotation (@RequiresPermission...).
    // KHÔNG có bean này, Spring hiểu '{value}' theo nghĩa đen -> check quyền tên "{value}" -> luôn 403.
    @Bean
    static AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }
}

package vn.com.ssv.master_data.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Meta-annotation gọn thay cho @PreAuthorize("@permissionChecker.has('...')")
// Dùng: @RequiresPermission("application:create") trên controller/service method
// '{value}' được thay bằng value() nhờ bean AnnotationTemplateExpressionDefaults (xem MethodSecurityConfig)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.has('{value}')")
public @interface RequiresPermission {

    String value();
}

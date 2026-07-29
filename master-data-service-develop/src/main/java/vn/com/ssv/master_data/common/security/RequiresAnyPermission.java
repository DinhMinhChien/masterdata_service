package vn.com.ssv.master_data.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cho qua nếu user có ÍT NHẤT 1 trong các quyền (OR).
// Dùng: @RequiresAnyPermission({"application:create", "application:update"})
// Cơ chế: String[] -> template nối thành "a,b" -> SpEL .split(',') -> mảng -> khớp hasAny(String...)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.hasAny('{value}'.split(','))")
public @interface RequiresAnyPermission {

    String[] value();
}

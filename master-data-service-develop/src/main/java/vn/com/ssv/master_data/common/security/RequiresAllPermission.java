package vn.com.ssv.master_data.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Cho qua nếu user có TẤT CẢ các quyền (AND).
// Dùng: @RequiresAllPermission({"application:read", "application:export"})
// Cơ chế: String[] -> template nối thành "a,b" -> SpEL .split(',') -> mảng -> khớp hasAll(String...)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.hasAll('{value}'.split(','))")
public @interface RequiresAllPermission {

    String[] value();
}

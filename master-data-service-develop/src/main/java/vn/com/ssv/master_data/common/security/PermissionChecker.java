package vn.com.ssv.master_data.common.security;

import org.springframework.stereotype.Component;

import java.util.Set;

import static vn.com.ssv.master_data.common.util.Const.ADMIN;
import static vn.com.ssv.master_data.common.util.SecurityUtil.getPermissions;
import static vn.com.ssv.master_data.common.util.SecurityUtil.getRoles;

// Kiểm tra quyền action-level. Dùng: @PreAuthorize("@permissionChecker.has('application:create')")
@Component("permissionChecker")
public class PermissionChecker {

    // có đúng 1 permission cụ thể
    public boolean has(String permissionCode) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        return getPermissions().contains(permissionCode);
    }

    // có ÍT NHẤT 1 trong các permission truyền vào (OR)
    public boolean hasAny(String... codes) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        Set<String> permissions = getPermissions();
        for (String code : codes) {
            if (permissions.contains(code)) {
                return true;
            }
        }
        return false;
    }

    // có TẤT CẢ permission truyền vào (AND)
    public boolean hasAll(String... codes) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        Set<String> permissions = getPermissions();
        for (String code : codes) {
            if (!permissions.contains(code)) {
                return false;
            }
        }
        return true;
    }
}

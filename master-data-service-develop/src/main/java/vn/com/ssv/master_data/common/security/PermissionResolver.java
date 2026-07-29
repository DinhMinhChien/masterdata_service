package vn.com.ssv.master_data.common.security;

import java.util.Collection;
import java.util.Set;

// Đổi tập role -> tập permission (cài đặt cụ thể: cache từ Admin Manager)
public interface PermissionResolver {

    Set<String> permissionsOf(Collection<String> roles);
}

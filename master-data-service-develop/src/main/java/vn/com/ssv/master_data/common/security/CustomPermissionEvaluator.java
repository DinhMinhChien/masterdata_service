package vn.com.ssv.master_data.common.security;

import org.jspecify.annotations.NonNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

// Mở rộng nếu cần check theo user- object ( user này có được action với bản ghi này không)
// Hiện tại các logic check này sẽ được xử lý ở service nếu có  => auto return true để bypass
// Quyết định kiến trúc: phân quyền chính của template là ACTION-LEVEL
// Chỉ triển khai class này nếu thực sự cần check quyền cấp-object qua @PreAuthorize:
//   @PreAuthorize("hasPermission(#id, 'Collateral', 'approve')")
//   -> khi đó load object theo id + áp ràng buộc, trả true/false.
// (class được cắm vào SpEL hasPermission(...) qua MethodSecurityConfig)
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    // dạng: hasPermission(#object, 'action') — object đã có sẵn
    @Override
    public boolean hasPermission(@NonNull Authentication auth, @NonNull Object targetDomainObject,
                                 @NonNull Object permission) {
        return true;
    }

    // dạng: hasPermission(#id, 'Collateral', 'action') — chỉ có id, tự load object
    @Override
    public boolean hasPermission(@NonNull Authentication auth, @NonNull Serializable targetId,
                                 @NonNull String targetType, @NonNull Object permission) {
        return true;
    }
}

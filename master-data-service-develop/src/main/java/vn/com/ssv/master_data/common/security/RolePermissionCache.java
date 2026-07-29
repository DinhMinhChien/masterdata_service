package vn.com.ssv.master_data.common.security;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.ssv.master_data.common.persistence.dto.RolePermissionDto;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.feature.client.AdminManagerClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

// Cache role -> permission, refresh định kỳ từ Admin Manager
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RolePermissionCache implements PermissionResolver {

    // Cách 2: role vừa được cấu hình bên Admin nhưng cache chưa có -> nạp lại ngay (không chờ chu kỳ),
    // nhưng chặn gọi Admin dồn dập bằng khoảng cách tối thiểu giữa 2 lần nạp lazy.
    static long LAZY_MIN_INTERVAL_MS = 10_000;

    // map role->permission lấy từ Admin Manager, swap nguyên khối khi reload -> request không thấy map dở dang
    // CÓ initializer -> final field này KHÔNG vào @RequiredArgsConstructor (tránh Spring đòi inject AtomicReference)
    AtomicReference<Map<String, Set<String>>> ref = new AtomicReference<>(Map.of());
    // thời điểm nạp gần nhất (start/scheduled/lazy) -> dùng để throttle nạp lazy
    AtomicLong lastReloadAt = new AtomicLong(0);
    AdminManagerClient adminManagerClient;

    // Từ list role truyền vào tra vào map để lấy ra list permission của user hiện tại
    @Override
    public Set<String> permissionsOf(Collection<String> roles) {
        Map<String, Set<String>> map = ref.get();
        // Cách 2: có role chưa nằm trong cache (vd mới tạo/mới gán quyền) -> thử nạp lại ngay (có throttle)
        if (hasUnknownRole(map, roles)) {
            map = reloadIfNotThrottled();
        }
        Set<String> result = new HashSet<>();
        for (String role : roles) {
            result.addAll(map.getOrDefault(role, Set.of()));
        }
        return result;
    }

    // nạp/refresh map từ Admin Manager (lúc start + định kỳ)
    @PostConstruct
    @Scheduled(fixedRateString = "${app.admin.refresh-ms:300000}")
    public void reload() {
        lastReloadAt.set(System.currentTimeMillis());
        List<RolePermissionDto> rolePermissionList = fetchFromAdmin();
        if (!rolePermissionList.isEmpty()) {
            // Tạo map role -> set permission
            Map<String, Set<String>> newMap = new HashMap<>();
            for (RolePermissionDto dto : rolePermissionList) {
                newMap.computeIfAbsent(dto.getRole(), k -> new HashSet<>())
                        .addAll(dto.getPermissions());
            }

            ref.set(newMap);

            log.info("Reloaded role-permission cache với {} roles", newMap.size());
        }
    }

    private List<RolePermissionDto> fetchFromAdmin() {
        try {
            ApiResponse<List<RolePermissionDto>> response = adminManagerClient.getAllRolePermissions();
            if (response != null && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi đến admin để lấy role-permission: {} ", e.getMessage());
        }
        return List.of();
    }

    // có ít nhất 1 role chưa xuất hiện trong cache
    private boolean hasUnknownRole(Map<String, Set<String>> map, Collection<String> roles) {
        for (String role : roles) {
            if (!map.containsKey(role)) {
                return true;
            }
        }
        return false;
    }

    // Nạp lại toàn bộ map nếu chưa nạp trong khoảng LAZY_MIN_INTERVAL_MS, đảm bảo chỉ 1 luồng nạp.
    private Map<String, Set<String>> reloadIfNotThrottled() {
        long now = System.currentTimeMillis();
        long last = lastReloadAt.get();
        // vừa nạp gần đây -> dùng cache hiện tại, tránh gọi Admin dồn dập (kể cả khi role thật sự không có quyền)
        if (now - last < LAZY_MIN_INTERVAL_MS) {
            return ref.get();
        }
        if (lastReloadAt.compareAndSet(last, now)) {
            reload();
        }
        return ref.get();
    }
}

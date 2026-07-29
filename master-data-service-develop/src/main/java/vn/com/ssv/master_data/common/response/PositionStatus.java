package vn.com.ssv.master_data.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionStatus {
    ACTIVE("Đang hoạt Động"),
    INACTIVE("Ngừng hoạt động");

    private final String name;
}

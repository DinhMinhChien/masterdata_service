package vn.com.ssv.master_data.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrganizationStatus {

    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động");

    private final String name;
}
package vn.com.ssv.master_data.feature.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.common.security.RequiresPermission;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;

import java.util.List;

import static vn.com.ssv.master_data.feature.constant.PermissionDefine.APPLICATION_CREATE;

@RequestMapping("/api/organizations")
public interface OrganizationApi {
    @Operation(summary = "Danh sách tổ chức")
    @GetMapping()
    @RequiresPermission(APPLICATION_CREATE)
    ApiResponse<PageResponse<OrganizationListResponse>> list(@RequestParam(defaultValue = "1") Integer page);

    @Operation(summary = "Chi tiết tổ chức")
    @GetMapping("/{id}")
    ApiResponse<OrganizationDetailResponse> getById(@PathVariable Long id);

    @Operation(summary ="Tạo tổ chức ")
    @PostMapping
    ApiResponse<OrganizationDetailResponse> create(@Valid @RequestBody OrganizationCreateRequest request);

    @Operation(summary ="Cập nhật tổ chức")
    @PutMapping("/{id}")
    ApiResponse<OrganizationDetailResponse> update(@PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequest request);

    @Operation(summary ="Xóa tổ chức")
    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id);

    @Operation(summary ="Cây tổ chức")
    @GetMapping("/tree")
    ApiResponse<List<OrganizationTreeResponse>> tree();

    @Operation(summary ="Tìm kiếm cây tổ chức")
    @PostMapping("/search")
    ApiResponse<PageResponse<OrganizationListResponse>> search(@RequestBody OrganizationSearchRequest request);

    @Operation(summary ="Tim kiem to chuc kem node cha")
    @PostMapping("/search-tree")
    ApiResponse<PageResponse<OrganizationListResponse>> searchTree(@RequestBody OrganizationSearchRequest request);

    @Operation(summary = "Danh sách đơn vị cha")

    @GetMapping("/parents")
    ApiResponse<PageResponse<OrganizationListResponse>> getParents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    );
    @Operation(summary = "Danh sách đơn vị con")
    @GetMapping("/children/{parentCode}")
    ApiResponse<List<OrganizationListResponse>> getChildren(@PathVariable String parentCode);
}

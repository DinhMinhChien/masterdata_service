package vn.com.ssv.master_data.feature.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.common.security.RequiresAnyPermission;
import vn.com.ssv.master_data.common.security.RequiresPermission;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;

import java.util.List;

import static vn.com.ssv.master_data.feature.constant.PermissionDefine.POSITION_CREATE;
import static vn.com.ssv.master_data.feature.constant.PermissionDefine.POSITION_DELETE;
import static vn.com.ssv.master_data.feature.constant.PermissionDefine.POSITION_GET_DETAIL;
import static vn.com.ssv.master_data.feature.constant.PermissionDefine.POSITION_SEARCH;
import static vn.com.ssv.master_data.feature.constant.PermissionDefine.POSITION_UPDATE;

@RequestMapping("/api/v1/positions")
public interface PositionApi {
    @Operation(summary = "Danh sách chức danh phân trang")
    @GetMapping
    @RequiresPermission(POSITION_SEARCH)
    ApiResponse<PageResponse<PositionListResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    );

    @Operation(summary = "Danh sách tất cả các chức danh")
    @GetMapping("/all")
    @RequiresPermission(POSITION_SEARCH)
    ApiResponse<List<PositionListResponse>> listAll();

    @Operation(summary = "Chi tiết chức danh")
    @GetMapping("/{id}")
    @RequiresAnyPermission({POSITION_GET_DETAIL, POSITION_SEARCH})
    ApiResponse<PositionDetailResponse> getById(@PathVariable Long id);

    @Operation(summary = "Tạo chức danh")
    @PostMapping
    @RequiresPermission(POSITION_CREATE)
    ApiResponse<PositionDetailResponse> create(@Valid @RequestBody PositionCreateRequest request);

    @Operation(summary = "Cập nhật chức danh")
    @PutMapping("/{id}")
    @RequiresPermission(POSITION_UPDATE)
    ApiResponse<PositionDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateRequest request
    );

    @Operation(summary = "Xóa chức danh")
    @DeleteMapping("/{id}")
    @RequiresPermission(POSITION_DELETE)
    ApiResponse<Void> delete(@PathVariable Long id);

    @Operation(summary = "Tìm kiếm chức danh")
    @PostMapping("/search")
    @RequiresPermission(POSITION_SEARCH)
    ApiResponse<PageResponse<PositionListResponse>> search(@RequestBody PositionSearchRequest request);
}

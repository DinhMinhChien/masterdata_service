package vn.com.ssv.master_data.feature.service;

import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;

import java.util.List;

public interface PositionService {
    // danh sách chức danh phân trang
    PageResponse<PositionListResponse> list(Integer page, Integer size);

    // danh sách chức danh không phân trang
    List<PositionListResponse> listAll();

    // chi tiết chức danh
    PositionDetailResponse getById(Long id);

    // tạo chức danh
    PositionDetailResponse create(PositionCreateRequest request);

    // cập nhật chức danh
    PositionDetailResponse update(Long id, PositionUpdateRequest request);

    // xóa chức danh
    void delete(Long id);

    // tìm kiếm chức danh
    PageResponse<PositionListResponse> search(PositionSearchRequest request);
}

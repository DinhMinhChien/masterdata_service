package vn.com.ssv.master_data.feature.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.common.response.ApiResponseFactory;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.controller.api.PositionApi;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;
import vn.com.ssv.master_data.feature.service.PositionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PositionController implements PositionApi {
    private final PositionService positionService;
    private final ApiResponseFactory apiResponseFactory;

    @Override
    public ApiResponse<PageResponse<PositionListResponse>> list(Integer page, Integer size) {
        return apiResponseFactory.success(positionService.list(page, size));
    }

    @Override
    public ApiResponse<List<PositionListResponse>> listAll() {
        return apiResponseFactory.success(positionService.listAll());
    }

    @Override
    public ApiResponse<PositionDetailResponse> getById(Long id) {
        return apiResponseFactory.success(positionService.getById(id));
    }

    @Override
    public ApiResponse<PositionDetailResponse> create(@Valid @RequestBody PositionCreateRequest request) {
        return apiResponseFactory.success(positionService.create(request));
    }

    @Override
    public ApiResponse<PositionDetailResponse> update(
            Long id,
            @Valid @RequestBody PositionUpdateRequest request
    ) {
        return apiResponseFactory.success(positionService.update(id, request));
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        positionService.delete(id);
        return apiResponseFactory.success();
    }

    @Override
    public ApiResponse<PageResponse<PositionListResponse>> search(PositionSearchRequest request) {
        return apiResponseFactory.success(positionService.search(request));
    }
}

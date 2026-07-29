package vn.com.ssv.master_data.feature.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ssv.master_data.common.response.ApiResponse;
import vn.com.ssv.master_data.common.response.ApiResponseFactory;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.controller.api.OrganizationApi;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;
import vn.com.ssv.master_data.feature.service.OrganizationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationController implements OrganizationApi {
    private  final OrganizationService organizationService;
    private final ApiResponseFactory apiResponseFactory;

    @Override
    public ApiResponse<PageResponse<OrganizationListResponse>> list(Integer page) {
        return apiResponseFactory.success(organizationService.finAll(page));
    }

    @Override
    public ApiResponse<OrganizationDetailResponse> getById(Long id) {
        return apiResponseFactory.success(organizationService.getById(id));
    }

    @Override
    public ApiResponse<OrganizationDetailResponse> create(@Valid @RequestBody OrganizationCreateRequest request) {
        return apiResponseFactory.success(organizationService.create(request));
    }


    @Override
    public ApiResponse<OrganizationDetailResponse> update(Long id, @Valid @RequestBody OrganizationUpdateRequest request) {
        return apiResponseFactory.success(organizationService.update(id, request));
    }

    @Override
    public ApiResponse<Void> delete(Long id) {organizationService.delete(id);
        return apiResponseFactory.success();
    }

    @Override
    public ApiResponse<List<OrganizationTreeResponse>> tree() {
        return apiResponseFactory.success(organizationService.getTree());
    }


    @Override
    public ApiResponse<PageResponse<OrganizationListResponse>> search(OrganizationSearchRequest request) {

        return apiResponseFactory.success(organizationService.search(request));
    }

    @Override
    public ApiResponse<PageResponse<OrganizationListResponse>> searchTree(OrganizationSearchRequest request) {

        return apiResponseFactory.success(organizationService.searchTree(request));
    }

    @Override
    public ApiResponse<PageResponse<OrganizationListResponse>> getParents(Integer page, Integer size) {
        return apiResponseFactory.success(organizationService.getParents(page, size));
    }

    @Override
    public ApiResponse<List<OrganizationListResponse>> getChildren(String parentCode) {
        return apiResponseFactory.success(organizationService.getChildren(parentCode));
    }
}

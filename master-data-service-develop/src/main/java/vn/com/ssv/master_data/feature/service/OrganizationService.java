package vn.com.ssv.master_data.feature.service;


import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;

import java.util.List;

public interface OrganizationService {
    PageResponse<OrganizationListResponse> finAll(Integer page);
    OrganizationDetailResponse getById(Long id);
    OrganizationDetailResponse create(OrganizationCreateRequest request);
    OrganizationDetailResponse update(Long id, OrganizationUpdateRequest request);
    void delete(long id);
    List<OrganizationTreeResponse> getTree();
    PageResponse<OrganizationListResponse> search(OrganizationSearchRequest request);
    PageResponse<OrganizationListResponse> searchTree(OrganizationSearchRequest request);

    PageResponse<OrganizationListResponse> getParents(Integer page, Integer size);

    List<OrganizationListResponse> getChildren(String parentCode);
}

package vn.com.ssv.master_data.feature.repository.custom;

import org.springframework.data.domain.Page;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;

public interface OrganizationRepositoryCustom {
    Page<OrganizationListResponse> search(OrganizationSearchRequest request);
    Page<OrganizationListResponse> searchTree(OrganizationSearchRequest request);
}

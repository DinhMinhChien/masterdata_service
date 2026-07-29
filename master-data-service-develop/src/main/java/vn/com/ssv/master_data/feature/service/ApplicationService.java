package vn.com.ssv.master_data.feature.service;

import vn.com.ssv.master_data.common.persistence.dto.PageDto;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.model.request.CreateApplicationRequest;
import vn.com.ssv.master_data.feature.model.response.ApplicationResponse;

public interface ApplicationService {

    ApplicationResponse create(CreateApplicationRequest request);

    ApplicationResponse getById(Long id);

    PageResponse<ApplicationResponse> search(PageDto request);
}

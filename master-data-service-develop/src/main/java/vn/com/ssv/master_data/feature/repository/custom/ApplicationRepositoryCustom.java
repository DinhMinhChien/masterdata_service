package vn.com.ssv.master_data.feature.repository.custom;

import org.springframework.data.domain.Page;
import vn.com.ssv.master_data.common.persistence.dto.PageDto;
import vn.com.ssv.master_data.feature.model.response.ApplicationResponse;


public interface ApplicationRepositoryCustom {
    Page<ApplicationResponse> search(PageDto request);
}

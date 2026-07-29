package vn.com.ssv.master_data.feature.repository.custom;

import org.springframework.data.domain.Page;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;

public interface PositionRepositoryCustom {
    Page<PositionListResponse> search(PositionSearchRequest request);
}

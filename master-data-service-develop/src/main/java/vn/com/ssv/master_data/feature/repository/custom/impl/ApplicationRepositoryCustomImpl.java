package vn.com.ssv.master_data.feature.repository.custom.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import vn.com.ssv.master_data.common.persistence.NativeQuerySupport;
import vn.com.ssv.master_data.common.persistence.dto.PageDto;
import vn.com.ssv.master_data.feature.model.response.ApplicationResponse;
import vn.com.ssv.master_data.feature.repository.custom.ApplicationRepositoryCustom;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ApplicationRepositoryCustomImpl extends NativeQuerySupport implements ApplicationRepositoryCustom {

    @Override
    public Page<ApplicationResponse> search(PageDto request) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, description, created_by, created_at, updated_by, updated_at FROM application WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(request.getSearch())) {
            sql.append("AND description ILIKE :search");
            params.put("search", appendLikeExpression(request.getSearch()));
        }
        sql.append(" ORDER BY id DESC");

        return getListPagination(sql.toString(), params, request, ApplicationResponse.class);
    }
}

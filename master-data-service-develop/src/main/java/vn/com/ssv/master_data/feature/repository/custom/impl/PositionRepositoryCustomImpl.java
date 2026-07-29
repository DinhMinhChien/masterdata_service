package vn.com.ssv.master_data.feature.repository.custom.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import vn.com.ssv.master_data.common.persistence.NativeQuerySupport;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;
import vn.com.ssv.master_data.feature.repository.custom.PositionRepositoryCustom;

import java.util.HashMap;
import java.util.Map;

import static vn.com.ssv.master_data.feature.constant.Const.ASC;
import static vn.com.ssv.master_data.feature.constant.Const.NOT_DELETED;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PositionRepositoryCustomImpl extends NativeQuerySupport implements PositionRepositoryCustom {
    @Override
    public Page<PositionListResponse> search(PositionSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("""
                SELECT p.id,
                       p.code,
                       p.name,
                       p.description,
                       p.status,
                       p.created_by,
                       p.created_at,
                       p.updated_by,
                       p.updated_at
                FROM positions p
                WHERE p.is_deleted = :isDeleted
                """);
        params.put("isDeleted", NOT_DELETED);

        if (StringUtils.isNotBlank(request.getKeyword())) {
            sql.append("""
                    AND (
                        LOWER(p.code) LIKE LOWER(:keyword)
                        OR LOWER(p.name) LIKE LOWER(:keyword)
                    )
                    """);
            params.put("keyword", appendLikeExpression(request.getKeyword()));
        }

        if (request.getStatus() != null) {
            sql.append("""
                    AND p.status = :status
                    """);
            params.put("status", request.getStatus().name());
        }

        applyDefaultSort(request);

        return getListPagination(
                sql.toString(),
                params,
                request,
                PositionListResponse.class
        );
    }

    private void applyDefaultSort(PositionSearchRequest request) {
        boolean hasMultiSort = request.getSorts() != null && !request.getSorts().isEmpty();
        boolean hasSingleSort = StringUtils.isNotBlank(request.getSort());

        if (!hasMultiSort && !hasSingleSort) {
            request.setSort("id");
            request.setDirection(ASC);
        }
    }
}

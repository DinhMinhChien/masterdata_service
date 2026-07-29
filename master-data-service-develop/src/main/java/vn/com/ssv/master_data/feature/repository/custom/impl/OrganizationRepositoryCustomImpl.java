package vn.com.ssv.master_data.feature.repository.custom.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import vn.com.ssv.master_data.common.persistence.NativeQuerySupport;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.repository.custom.OrganizationRepositoryCustom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class OrganizationRepositoryCustomImpl extends NativeQuerySupport implements OrganizationRepositoryCustom {
    @Override
    public Page<OrganizationListResponse> search(OrganizationSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("""
                 SELECT   o.id, o.code,  o.name,  o.status,ot.code AS type_code, ot.name AS type_name,
                                                      p.code AS parent_code,
                                                      p.name AS parent_name
                                                      FROM organization o
                                                      LEFT JOIN organization_type ot
                                                      ON o.type_id = ot.id
                                                      LEFT JOIN organization p
                                                     ON o.parent_id = p.id
                                                     WHERE o.is_deleted = 0
                 """);
        // Tìm kiếm theo theo keyword  tìm theo 2 fileds : mã tổ chức và tên tổ chức
        if (StringUtils.isNotBlank(request.getKeyword())) {
            sql.append("""
           AND (
            LOWER(o.code) LIKE LOWER(:keyword)
            OR LOWER(o.name) LIKE LOWER(:keyword)
               )
           """);
            params.put("keyword", appendLikeExpression(request.getKeyword()));}

        // Tìm kiếm theo loại phòng ban
        if (StringUtils.isNotBlank(request.getTypeCode())) {

            sql.append("""
                          AND ot.code = :typeCode
                         """);
            params.put("typeCode", request.getTypeCode());
        }
        // Tìm kiếm theo trangj thái
        if (StringUtils.isNotBlank(request.getStatus())) {
            sql.append("""
                AND o.status = :status
                """);

            params.put("status", request.getStatus());
        }
        // Tìm kiếm theo thằng con
        if (StringUtils.isNotBlank(request.getParentCode())) {

            sql.append("""
                AND p.code = :parentCode
                """);

            params.put("parentCode", request.getParentCode());
        }
        return getListPagination(
                sql.toString(),
                params,
                request,
                OrganizationListResponse.class
        );

    }

    @Override
    public Page<OrganizationListResponse> searchTree(OrganizationSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder matchedWhere = new StringBuilder("""
                WHERE o.is_deleted = 0
                """);

        if (StringUtils.isNotBlank(request.getKeyword())) {
            matchedWhere.append("""
                    AND (
                        LOWER(o.code) LIKE LOWER(:keyword)
                        OR LOWER(o.name) LIKE LOWER(:keyword)
                    )
                    """);
            params.put("keyword", appendLikeExpression(request.getKeyword()));
        }

        if (StringUtils.isNotBlank(request.getTypeCode())) {
            matchedWhere.append("""
                    AND ot.code = :typeCode
                    """);
            params.put("typeCode", request.getTypeCode());
        }

        if (StringUtils.isNotBlank(request.getStatus())) {
            matchedWhere.append("""
                    AND o.status = :status
                    """);
            params.put("status", request.getStatus());
        }

        if (StringUtils.isNotBlank(request.getParentCode())) {
            matchedWhere.append("""
                    AND p.code = :parentCode
                    """);
            params.put("parentCode", request.getParentCode());
        }

        String sql = """
                WITH matched AS (
                    SELECT o.path
                    FROM organization o
                    LEFT JOIN organization_type ot ON o.type_id = ot.id
                    LEFT JOIN organization p ON o.parent_id = p.id
                    %s
                ),
                ancestor_codes AS (
                    SELECT DISTINCT unnest(string_to_array(path, '/')) AS code
                    FROM matched
                    WHERE path IS NOT NULL
                )
                SELECT o.id,
                       o.code,
                       o.name,
                       o.status,
                       ot.code AS type_code,
                       ot.name AS type_name,
                       p.code AS parent_code,
                       p.name AS parent_name
                FROM organization o
                LEFT JOIN organization_type ot ON o.type_id = ot.id
                LEFT JOIN organization p ON o.parent_id = p.id
                INNER JOIN ancestor_codes ac ON ac.code = o.code
                WHERE o.is_deleted = 0
                """.formatted(matchedWhere);

        return getListPagination(
                sql,
                params,
                request,
                OrganizationListResponse.class
        );
    }
}

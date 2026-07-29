package vn.com.ssv.master_data.feature.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.ssv.master_data.common.persistence.BaseRepository;
import vn.com.ssv.master_data.feature.entity.Organization;
import vn.com.ssv.master_data.feature.repository.custom.OrganizationRepositoryCustom;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends BaseRepository<Organization, Long>, OrganizationRepositoryCustom {

    boolean existsByCode(String code);
    Optional<Organization> findByCode(String code);
    Page<Organization> findByIsDeleted(Integer isDeleted, Pageable pageable);
    Optional<Organization> findByIdAndIsDeleted(Long id, Integer isDeleted);
    @Query("""
    select o from Organization o where o.id = :id and o.isDeleted = 0""")
    Optional<Organization> findActiveById(@Param("id") Long id);
    List<Organization> findByIsDeleted(Integer isDeleted);
    // QUERY danh sách thằng cha
    @Query(
            value = """
                  SELECT * FROM organization o  WHERE o.parent_id IS NULL AND o.is_deleted = 0 ORDER BY o.id """,
            countQuery = """
                SELECT COUNT(*) FROM organization o WHERE o.parent_id IS NULL AND o.is_deleted = 0 """, nativeQuery = true )
    Page<Organization> findParents(Pageable pageable);

     // Query tìm đến danh sách thằng con
    @Query(value = """
        SELECT o.* FROM organization o INNER JOIN organization p ON o.parent_id = p.id
        WHERE p.code = :parentCode AND o.is_deleted = 0
                ORDER BY o.id  """, nativeQuery = true)
    List<Organization> findChildrenByParentCode(@Param("parentCode") String parentCode);

}
